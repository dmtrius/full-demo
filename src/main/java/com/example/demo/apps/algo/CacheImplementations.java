package com.example.demo.apps.algo;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.IO.println;

// Interface for all cache implementations
interface Cache<K, V> {
    void put(K key, V value);
    V get(K key);
    void remove(K key);
    int size();
    @SuppressWarnings("unused")
    void clear();
}

/**
 * <b>SIMPLE CACHE</b>
 */
class SimpleCache<K, V> implements Cache<K, V> {
    private final Map<K, V> cache;
    private final int capacity;

    public SimpleCache(int capacity) {
        this.capacity = capacity;
        this.cache = HashMap.newHashMap(capacity);
    }

    @Override
    public void put(K key, V value) {
        if (cache.size() >= capacity) {
            // Remove random entry when capacity is reached
            cache.keySet().iterator().next();
        }
        cache.put(key, value);
    }

    @Override
    public V get(K key) {
        return cache.get(key);
    }

    @Override
    public void remove(K key) {
        cache.remove(key);
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public void clear() {
        cache.clear();
    }
}

/**
 * <b>LRU Cache</b> using <i>LinkedHashMap</i>
 */
class LRUCache<K, V> implements Cache<K, V> {
    private final Map<K, V> cache;
    private final int capacity;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        // Access-order LinkedHashMap for LRU
        this.cache = new LinkedHashMap<>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > LRUCache.this.capacity;
            }
        };
    }

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
    }

    @Override
    public V get(K key) {
        return cache.get(key);
    }

    @Override
    public void remove(K key) {
        cache.remove(key);
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public void clear() {
        cache.clear();
    }
}

/**
 * <b>Concurrent LRU Cache</b> using <i>LinkedHashMap</i> and <i>ConcurrentLinkedDeque</i>
 */
class ConcurrentLRUCache<K, V> implements Cache<K, V> {

    private final int capacity;
    private final ConcurrentHashMap<K, V> map = new ConcurrentHashMap<>();
    private final ConcurrentLinkedDeque<K> deque = new ConcurrentLinkedDeque<>();

    public ConcurrentLRUCache(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public V get(K key) {
        V value = map.get(key);
        if (!Objects.isNull(value)) {
            deque.remove(key);
            deque.addLast(key);
        }
        return value;
    }

    @Override
    public void put(K key, V value) {
        if (map.put(key, value) == null) {
            deque.addLast(key);
        } else {
            deque.remove(key);
            deque.addLast(key);
        }

        evictIfNeeded();
    }

    private void evictIfNeeded() {
        while (map.size() > capacity) {
            K eldest = deque.pollFirst();
            if (!Objects.isNull(eldest)) {
                map.remove(eldest);
            }
        }
    }

    @Override
    public void remove(K key) {
        map.remove(key);
        deque.remove(key);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public void clear() {
        map.clear();
        deque.clear();
    }
}

/**
 * <b>LFU Cache</b> using <i>PriorityQueue</i> and <i>HashMaps</i>
 */
class LFUCache<K, V> implements Cache<K, V> {
    private static class CacheEntry<K, V> {
        K key;
        V value;
        int frequency;
        long lastUsed;

        CacheEntry(K key, V value) {
            this.key = key;
            this.value = value;
            this.frequency = 1;
            this.lastUsed = System.nanoTime();
        }
    }

    private final int capacity;
    private final Map<K, CacheEntry<K, V>> cache;
    private final PriorityQueue<CacheEntry<K, V>> frequencyQueue;
    private final AtomicLong timestamp;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>();
        this.frequencyQueue = new PriorityQueue<>(
                Comparator.comparingInt((CacheEntry<K, V> a) -> a.frequency)
                        .thenComparingLong(a -> a.lastUsed));
        this.timestamp = new AtomicLong();
    }

    @Override
    public void put(K key, V value) {
        if (capacity <= 0) {
            return;
        }

        synchronized (this) {
            CacheEntry<K, V> entry = cache.get(key);
            if (!Objects.isNull(entry)) {
                // Update existing entry
                entry.value = value;
                entry.frequency++;
                entry.lastUsed = timestamp.incrementAndGet();
                frequencyQueue.remove(entry);
                frequencyQueue.offer(entry);
            } else {
                // Evict if necessary
                if (cache.size() >= capacity) {
                    CacheEntry<K, V> lfu = frequencyQueue.poll();
                    if (!Objects.isNull(lfu)) {
                        cache.remove(lfu.key);
                    }
                }
                // Add new entry
                entry = new CacheEntry<>(key, value);
                cache.put(key, entry);
                frequencyQueue.offer(entry);
            }
        }
    }

    @Override
    public V get(K key) {
        synchronized (this) {
            CacheEntry<K, V> entry = cache.get(key);
            if (!Objects.isNull(entry)) {
                // Update frequency and timestamp
                entry.frequency++;
                entry.lastUsed = timestamp.incrementAndGet();
                frequencyQueue.remove(entry);
                frequencyQueue.offer(entry);
                return entry.value;
            }
            return null;
        }
    }

    @Override
    public void remove(K key) {
        synchronized (this) {
            CacheEntry<K, V> entry = cache.remove(key);
            if (!Objects.isNull(entry)) {
                frequencyQueue.remove(entry);
            }
        }
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public void clear() {
        synchronized (this) {
            cache.clear();
            frequencyQueue.clear();
        }
    }
}

/**
 * <b>TTL Cache</b> with <i>ConcurrentHashMap</i>
  */
class TTLCache<K, V> implements Cache<K, V> {
    private static class CacheEntry<V> {
        V value;
        long expiryTime;

        CacheEntry(V value, long ttlMillis) {
            this.value = value;
            this.expiryTime = System.currentTimeMillis() + ttlMillis;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    private final ConcurrentHashMap<K, CacheEntry<V>> cache;
    private final long ttlMillis;

    public TTLCache(long ttlMillis, boolean isAutoClean) {
        this.cache = new ConcurrentHashMap<>();
        this.ttlMillis = ttlMillis;

        // Optional: Periodic cleanup
        if (isAutoClean) {
            println("start cleaner...");
            try (var exc = Executors.newSingleThreadScheduledExecutor()) {
                exc.scheduleAtFixedRate(() -> cache.entrySet().removeIf(
                        entry -> entry.getValue().isExpired()),
                        ttlMillis, ttlMillis, TimeUnit.MILLISECONDS);
            }
        }
    }

    @Override
    public void put(K key, V value) {
        cache.put(key, new CacheEntry<>(value, ttlMillis));
    }

    @Override
    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (!Objects.isNull(entry) && !entry.isExpired()) {
            return entry.value;
        }
        cache.remove(key);
        return null;
    }

    @Override
    public void remove(K key) {
        cache.remove(key);
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public void clear() {
        cache.clear();
    }
}

/**
 * <b>Lock Free RLU-TTL Cache</b>
 */
class LockFreeTTLCache<K, V> {

    private final int maxSize;
    private final long ttlMillis;

    private final ConcurrentHashMap<K, Node<K, V>> map = new ConcurrentHashMap<>();

    // LRU list (lock-free)
    private final AtomicReference<Node<K, V>> head = new AtomicReference<>();
    private final AtomicReference<Node<K, V>> tail = new AtomicReference<>();

    public LockFreeTTLCache(int maxSize, Duration ttl, boolean isAutoClean) {
        this.maxSize = maxSize;
        this.ttlMillis = ttl.toMillis();
        if (isAutoClean) {
            startExpiryCleaner();
        }
    }

    static final class Node<K, V> {
        final K key;
        final V value;
        final long expiryTime;

        final AtomicReference<Node<K, V>> prev = new AtomicReference<>();
        final AtomicReference<Node<K, V>> next = new AtomicReference<>();

        Node(K key, V value, long expiryTime) {
            this.key = key;
            this.value = value;
            this.expiryTime = expiryTime;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    public V get(K key) {
        Node<K, V> node = map.get(key);
        if (Objects.isNull(node)) {
            return null;
        }

        if (node.isExpired()) {
            removeNode(node);
            map.remove(key, node);
            return null;
        }

        moveToTail(node); // LRU update
        return node.value;
    }

    public void put(K key, V value) {
        long expiry = System.currentTimeMillis() + ttlMillis;
        Node<K, V> newNode = new Node<>(key, value, expiry);

        Node<K, V> existing = map.put(key, newNode);
        if (!Objects.isNull(existing)) {
            removeNode(existing);
        }

        appendToTail(newNode);
        evictIfNeeded();
    }

    private void appendToTail(Node<K, V> node) {
        while (true) {
            Node<K, V> currentTail = tail.get();
            node.prev.set(currentTail);
            node.next.set(null);

            if (tail.compareAndSet(currentTail, node)) {
                if (currentTail == null) {
                    head.set(node);
                } else {
                    currentTail.next.set(node);
                }
                return;
            }
        }
    }

    private void moveToTail(Node<K, V> node) {
        removeNode(node);
        appendToTail(node);
    }

    private void removeNode(Node<K, V> node) {
        Node<K, V> prev = node.prev.get();
        Node<K, V> next = node.next.get();

        if (prev != null) {
            prev.next.compareAndSet(node, next);
        } else {
            head.compareAndSet(node, next);
        }

        if (next != null) {
            next.prev.compareAndSet(node, prev);
        } else {
            tail.compareAndSet(node, prev);
        }

        node.prev.set(null);
        node.next.set(null);
    }

    private void evictIfNeeded() {
        while (map.size() > maxSize) {
            Node<K, V> lru = head.get();
            if (lru == null) return;

            if (map.remove(lru.key, lru)) {
                removeNode(lru);
            }
        }
    }

    public void startExpiryCleaner() {
        try(var exc = Executors.newSingleThreadScheduledExecutor()) {
            println("start cleaner...");
            exc.scheduleAtFixedRate(() -> {
                for (var entry : map.values()) {
                    if (entry.isExpired()) {
                        map.remove(entry.key, entry);
                        removeNode(entry);
                    }
                }
            }, 1, 1, TimeUnit.SECONDS);
        }
    }
}

// Main class to demonstrate usage
@Slf4j
public class CacheImplementations {
    private static final String KEY_ONE = "1";
    private static final String KEY_TWO = "2";
    private static final String KEY_THREE = "3";
    private static final String VALUE_ONE = "One";
    private static final String VALUE_TWO = "Two";
    private static final String VALUE_THREE = "Three";
    private static final String GET_ONE = "Get 1: ";
    private static final String GET_TWO = "Get 2: ";
    private static final String GET_THREE = "Get 3: ";

    private static final int CAPACITY = 2;

    @SneakyThrows
    void main() {
        // Simple Cache Demo
        println("Simple Cache Demo:");
        Cache<String, String> simpleCache = new SimpleCache<>(CAPACITY);
        simpleCache.put(KEY_ONE, VALUE_ONE);
        simpleCache.put(KEY_TWO, VALUE_TWO);
        simpleCache.put(KEY_THREE, VALUE_THREE); // This should evict an entry
        println(GET_ONE + simpleCache.get(KEY_ONE));
        println(GET_TWO + simpleCache.get(VALUE_TWO));
        println(GET_THREE + simpleCache.get(VALUE_THREE));

        // LRU Cache Demo
        println("\nLRU Cache Demo:");
        Cache<String, String> lruCache = new LRUCache<>(CAPACITY);
        lruCache.put(KEY_ONE, VALUE_ONE);
        lruCache.put(KEY_TWO, VALUE_TWO);
        lruCache.get(KEY_ONE); // Make "1" recently used
        lruCache.put(KEY_THREE, VALUE_THREE); // Should evict "2"
        println(GET_ONE + lruCache.get(KEY_ONE));
        println(GET_TWO + lruCache.get(KEY_TWO));
        println(GET_THREE + lruCache.get(KEY_THREE));

        //Concurrent LRU Cache Demo
        println("\nConcurrent LRU Cache Demo:");
        Cache<String, String> clruCache = new ConcurrentLRUCache<>(CAPACITY);
        clruCache.put(KEY_ONE, VALUE_ONE);
        clruCache.put(KEY_TWO, VALUE_TWO);
        clruCache.get(KEY_ONE); // Make "1" recently used
        clruCache.put(KEY_THREE, VALUE_THREE); // Should evict "2"
        println(GET_ONE + clruCache.get(KEY_ONE));
        println(GET_TWO + clruCache.get(KEY_TWO));
        println(GET_THREE + clruCache.get(KEY_THREE));

        // LFU Cache Demo
        println("\nLFU Cache Demo:");
        Cache<String, String> lfuCache = new LFUCache<>(CAPACITY);
        lfuCache.put(KEY_ONE, VALUE_ONE);
        lfuCache.get(KEY_ONE); // freq=2
        lfuCache.put(KEY_TWO, VALUE_TWO); // freq=1
        lfuCache.put(KEY_THREE, VALUE_THREE); // Should evict "2" (lower frequency)
        println(GET_ONE + lfuCache.get(KEY_ONE));
        println(GET_TWO + lfuCache.get(KEY_TWO));
        println(GET_THREE + lfuCache.get(KEY_THREE));

        // TTL Cache Demo
        println("\nTTL Cache Demo:");
        Cache<String, String> ttlCache = new TTLCache<>(
                1000, true); // 1 second TTL
        ttlCache.put(KEY_ONE, VALUE_ONE);
        println("Get 1 (immediate): " + ttlCache.get(KEY_ONE));
        println("Get 2 (immediate): " + ttlCache.get(KEY_TWO));
        // Wait for expiration
        Thread.sleep(1500);
        println("Get 1 (after TTL): " + ttlCache.get(KEY_ONE));

        // Lock free LRU-TTL Cache Demo
        println("\nLock Free LRU-TTL Cache Demo:");
        LockFreeTTLCache<String, String> lfCache = new LockFreeTTLCache<>(CAPACITY,
                Duration.ofMillis(1000), true);
        lfCache.put(KEY_ONE, VALUE_ONE);
        println("Get 1 (immediate): " + lfCache.get(KEY_ONE));
        println("Get 2 (immediate): " + lfCache.get(KEY_TWO));
        // Wait for expiration
        Thread.sleep(1500);
        println("Get 1 (after TTL): " + lfCache.get(KEY_ONE));
    }
}
