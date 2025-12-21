package com.example.demo.apps.algo;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

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
            new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(ttlMillis);
                        cache.entrySet().removeIf(
                                entry -> entry.getValue().isExpired());
                    } catch (InterruptedException _) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }).start();
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

// Main class to demonstrate usage
@Slf4j
public class CacheImplementations {
    @SneakyThrows
    void main() {
        // Simple Cache Demo
        println("Simple Cache Demo:");
        Cache<String, String> simpleCache = new SimpleCache<>(2);
        simpleCache.put("1", "One");
        simpleCache.put("2", "Two");
        simpleCache.put("3", "Three"); // This should evict an entry
        println("Get 1: " + simpleCache.get("1"));
        println("Get 2: " + simpleCache.get("2"));
        println("Get 3: " + simpleCache.get("3"));

        // LRU Cache Demo
        println("\nLRU Cache Demo:");
        Cache<String, String> lruCache = new LRUCache<>(2);
        lruCache.put("1", "One");
        lruCache.put("2", "Two");
        lruCache.get("1"); // Make "1" recently used
        lruCache.put("3", "Three"); // Should evict "2"
        println("Get 1: " + lruCache.get("1"));
        println("Get 2: " + lruCache.get("2"));
        println("Get 3: " + lruCache.get("3"));

        // LFU Cache Demo
        println("\nLFU Cache Demo:");
        Cache<String, String> lfuCache = new LFUCache<>(2);
        lfuCache.put("1", "One");
        lfuCache.get("1"); // freq=2
        lfuCache.put("2", "Two"); // freq=1
        lfuCache.put("3", "Three"); // Should evict "2" (lower frequency)
        println("Get 1: " + lfuCache.get("1"));
        println("Get 2: " + lfuCache.get("2"));
        println("Get 3: " + lfuCache.get("3"));

        // TTL Cache Demo
        println("\nTTL Cache Demo:");
        Cache<String, String> ttlCache = new TTLCache<>(1000, false); // 1 second TTL
        ttlCache.put("1", "One");
        println("Get 1 (immediate): " + ttlCache.get("1"));

        Thread.sleep(1500); // Wait for expiration

        println("Get 1 (after TTL): " + ttlCache.get("1"));
    }
}
