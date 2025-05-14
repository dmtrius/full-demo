package com.example.demo.apps.algo;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Supplier;

public class TTLCacheScoped<K, V> {
    private final ConcurrentMap<K, CacheEntry<V>> cache;
    private final long ttlMillis;
    private final AtomicLong lastCleanup;
    // ScopedValue for thread-local context (e.g., request ID or user context)
    private static final ScopedValue<String> OPERATION_CONTEXT = ScopedValue.newInstance();

    /**
     * @param context Store context for debugging or logging
     */ // Inner class to hold value and expiration timestamp
        private record CacheEntry<V>(V value, long expiryTime, String context) {
    }

    // Constructor: ttlSeconds defines how long entries live
    public TTLCacheScoped(long ttlSeconds) {
        this.cache = new ConcurrentHashMap<>();
        this.ttlMillis = ttlSeconds * 1000;
        this.lastCleanup = new AtomicLong(System.currentTimeMillis());
    }

    // Put a value in the cache with the specified TTL, using current scoped context
    public void put(K key, V value) {
        String context = OPERATION_CONTEXT.isBound() ? OPERATION_CONTEXT.get() : "default";
        cleanupIfNeeded();
        cache.put(key, new CacheEntry<>(value, System.currentTimeMillis() + ttlMillis, context));
    }

    // Get a value from the cache, returns null if expired or not found
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
        // Verify context if needed (e.g., for logging or access control)
        String currentContext = OPERATION_CONTEXT.isBound() ? OPERATION_CONTEXT.get() : "default";
        System.out.println("Accessing key " + key + " with context: " + entry.context +
                ", current context: " + currentContext);
        return entry.value;
    }

    // Compute value if absent or expired, using the provided function
    public V computeIfAbsent(K key, Function<K, V> mappingFunction) {
        cleanupIfNeeded();
        String context = OPERATION_CONTEXT.isBound() ? OPERATION_CONTEXT.get() : "default";
        CacheEntry<V> entry = cache.get(key);
        if (entry == null || entry.expiryTime < System.currentTimeMillis()) {
            V newValue = mappingFunction.apply(key);
            cache.put(key, new CacheEntry<>(newValue, System.currentTimeMillis() + ttlMillis, context));
            return newValue;
        }
        return entry.value;
    }

    // Run an operation with a specific scoped context
    public <T> T runWithContext(String context, Supplier<T> operation) {
        return ScopedValue.where(OPERATION_CONTEXT, context).call(operation::get);
    }

    // Remove a specific key from the cache
    public void remove(K key) {
        cache.remove(key);
    }

    // Clear the entire cache
    public void clear() {
        cache.clear();
    }

    // Get current size of the cache (may include expired entries before cleanup)
    public int size() {
        cleanupIfNeeded();
        return cache.size();
    }

    // Periodic cleanup of expired entries
    private void cleanupIfNeeded() {
        long currentTime = System.currentTimeMillis();
        // Cleanup every 10% of TTL duration
        if (currentTime - lastCleanup.get() > ttlMillis / 10) {
            if (lastCleanup.compareAndSet(lastCleanup.get(), currentTime)) {
                cache.entrySet().removeIf(entry ->
                        entry.getValue().expiryTime < currentTime);
            }
        }
    }
}
