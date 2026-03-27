package com.example.demo.apps.algo;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TTLCacheStructuredConcurrency<K, V> {
    private final ConcurrentMap<K, CacheEntry<V>> cache;
    private final long ttlMillis;
    private final AtomicLong lastCleanup;
    private static final ScopedValue<String> OPERATION_CONTEXT = ScopedValue.newInstance();

    /**
     * @param context Store context for debugging or logging
     */
        private record CacheEntry<V>(V value, long expiryTime, String context) {
    }

    public TTLCacheStructuredConcurrency(long ttlSeconds) {
        this.cache = new ConcurrentHashMap<>();
        this.ttlMillis = ttlSeconds * 1000;
        this.lastCleanup = new AtomicLong(System.currentTimeMillis());
    }

    public void put(K key, V value) {
        String context = OPERATION_CONTEXT.isBound() ? OPERATION_CONTEXT.get() : "default";
        cleanupIfNeeded();
        cache.put(key, new CacheEntry<>(value, System.currentTimeMillis() + ttlMillis, context));
    }

    public V get(K key) {
        cleanupIfNeeded();
        CacheEntry<V> entry = cache.get(key);
        if (entry == null) {
            return null;
        }
        if (entry.expiryTime < System.currentTimeMillis()) {
            cache.remove(key);
            return null;
        }
        String currentContext = OPERATION_CONTEXT.isBound() ? OPERATION_CONTEXT.get() : "default";
        System.out.println("Accessing key " + key + " with context: " + entry.context +
                ", current context: " + currentContext);
        return entry.value;
    }

    public V computeIfAbsent(K key, Function<K, V> mappingFunction) {
        cleanupIfNeeded();
        String context = OPERATION_CONTEXT.isBound() ? OPERATION_CONTEXT.get() : "default";
        CacheEntry<V> entry = cache.get(key);
        if (Objects.isNull(entry) || entry.expiryTime < System.currentTimeMillis()) {
            V newValue = mappingFunction.apply(key);
            cache.put(key, new CacheEntry<>(newValue, System.currentTimeMillis() + ttlMillis, context));
            return newValue;
        }
        return entry.value;
    }

    public <T> T runWithContext(String context, Supplier<T> operation) {
        return ScopedValue.where(OPERATION_CONTEXT, context).call(operation::get);
    }

    public void batchPut(Map<K, V> entries) {
        String context = OPERATION_CONTEXT.isBound() ? OPERATION_CONTEXT.get() : "default";
        try (var scope = StructuredTaskScope.open()) {
            entries.entrySet().stream()
                    .map(entry -> scope.fork(() -> {
                        put(entry.getKey(), entry.getValue());
                        return null;
                    }))
                    .toList();
            scope.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Batch put interrupted", e);
        }
    }

    public Map<K, V> batchGet(List<K> keys) {
        try (var scope = StructuredTaskScope.open()) {
            List<StructuredTaskScope.Subtask<V>> tasks = keys.stream()
                    .map(key -> scope.fork(() -> get(key)))
                    .toList();
            scope.join();
            return keys.stream()
                    .collect(Collectors.toMap(
                            key -> key,
                            key -> tasks.get(keys.indexOf(key)).get(),
                            (v1, v2) -> v1,
                            ConcurrentHashMap::new
                    ));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Batch get interrupted", e);
        }
    }

    public void remove(K key) {
        cache.remove(key);
    }

    public void clear() {
        cache.clear();
    }

    public int size() {
        cleanupIfNeeded();
        return cache.size();
    }

    private void cleanupIfNeeded() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCleanup.get() > ttlMillis / 10) {
            if (lastCleanup.compareAndSet(lastCleanup.get(), currentTime)) {
                cache.entrySet().removeIf(entry ->
                        entry.getValue().expiryTime < currentTime);
            }
        }
    }
}
