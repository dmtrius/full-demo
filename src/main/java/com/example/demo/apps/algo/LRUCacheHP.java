package com.example.demo.apps.algo;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Random;

/**
 * High-performance thread-safe LRU Cache implementation in Java
 * Combines ConcurrentHashMap for fast lookups with a doubly linked list
 * and read-write locks for thread-safe LRU ordering.
 */
public class LRUCacheHP<K, V> {

    /**
     * Internal node class for the doubly linked list
     */
    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> prev;
        Node<K, V> next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final ConcurrentHashMap<K, Node<K, V>> cache;
    private final Node<K, V> head;  // Dummy head
    private final Node<K, V> tail;  // Dummy tail
    private final ReentrantReadWriteLock lock;
    private final ReentrantReadWriteLock.ReadLock readLock;
    private final ReentrantReadWriteLock.WriteLock writeLock;
    private final AtomicInteger size;

    /**
     * Creates a new LRU cache with the specified capacity
     * @param capacity Maximum number of entries the cache can hold
     */
    public LRUCacheHP(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }

        this.capacity = capacity;
        this.cache = new ConcurrentHashMap<>(capacity);
        this.head = new Node<>(null, null);
        this.tail = new Node<>(null, null);
        this.lock = new ReentrantReadWriteLock();
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();
        this.size = new AtomicInteger(0);

        // Initialize the doubly linked list
        head.next = tail;
        tail.prev = head;
    }

    /**
     * Retrieves a value by key, marking it as most recently used
     * @param key The key to look up
     * @return The value associated with the key, or null if not found
     */
    public V get(K key) {
        if (key == null) return null;

        Node<K, V> node = cache.get(key);
        if (node == null) {
            return null;
        }

        // Move to head (most recently used)
        moveToHead(node);
        return node.value;
    }

    /**
     * Puts a key-value pair into the cache
     * @param key The key
     * @param value The value
     */
    public void put(K key, V value) {
        if (key == null) return;

        Node<K, V> existing = cache.get(key);
        if (existing != null) {
            // Update existing node
            existing.value = value;
            moveToHead(existing);
        } else {
            // Create a new node
            Node<K, V> newNode = new Node<>(key, value);

            // Check if we need to evict
            if (size.get() >= capacity) {
                Node<K, V> lru = removeTail();
                cache.remove(lru.key);
                size.decrementAndGet();
            }

            cache.put(key, newNode);
            addToHead(newNode);
            size.incrementAndGet();
        }
    }

    /**
     * Removes a key from the cache
     * @param key The key to remove
     * @return The value which was removed, or null if key wasn't present
     */
    public V remove(K key) {
        if (key == null) return null;

        Node<K, V> node = cache.remove(key);
        if (node != null) {
            removeFromList(node);
            size.decrementAndGet();
            return node.value;
        }
        return null;
    }

    /**
     * Returns the current size of the cache
     */
    public int size() {
        return size.get();
    }

    /**
     * Checks if the cache is empty
     */
    public boolean isEmpty() {
        return size.get() == 0;
    }

    /**
     * Clears all entries from the cache
     */
    public void clear() {
        writeLock.lock();
        try {
            cache.clear();
            head.next = tail;
            tail.prev = head;
            size.set(0);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Returns a set of all keys currently in the cache
     */
    public Set<K> keySet() {
        return new HashSet<>(cache.keySet());
    }

    /**
     * Checks if the cache contains the specified key
     */
    public boolean containsKey(K key) {
        return cache.containsKey(key);
    }

    /**
     * Returns the maximum capacity of the cache
     */
    public int capacity() {
        return capacity;
    }

    // Private helper methods for list manipulation

    private void addToHead(Node<K, V> node) {
        writeLock.lock();
        try {
            node.prev = head;
            node.next = head.next;
            head.next.prev = node;
            head.next = node;
        } finally {
            writeLock.unlock();
        }
    }

    private void removeFromList(Node<K, V> node) {
        writeLock.lock();
        try {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        } finally {
            writeLock.unlock();
        }
    }

    private void moveToHead(Node<K, V> node) {
        removeFromList(node);
        addToHead(node);
    }

    private Node<K, V> removeTail() {
        writeLock.lock();
        try {
            Node<K, V> last = tail.prev;
            removeFromList(last);
            return last;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public String toString() {
        return String.format("LRUCache{size=%d, capacity=%d}", size(), capacity);
    }

    // Example usage and performance testing
    @SuppressWarnings("unused")
    public static void main(String[] args) throws InterruptedException {
        // Basic functionality test
        LRUCacheHP<String, Integer> cache = new LRUCacheHP<>(3);

        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);

        System.out.println("Get 'a': " + cache.get("a"));  // 1
        System.out.println("Size: " + cache.size());        // 3

        cache.put("d", 4);  // Should evict 'b'
        System.out.println("Get 'b': " + cache.get("b"));  // null
        System.out.println("Keys: " + cache.keySet());      // [a, c, d]

        // Concurrent performance test
        System.out.println("\nRunning concurrent performance test...");

        LRUCache<Integer, String> concurrentCache = new LRUCache<>(1000);
        ExecutorService executor = Executors.newFixedThreadPool(10);
        Random random = new Random();

        long startTime = System.nanoTime();

        CompletableFuture<?>[] futures = new CompletableFuture[10];
        for (int threadId = 0; threadId < 10; threadId++) {
            final int id = threadId;
            futures[threadId] = CompletableFuture.runAsync(() -> {
                for (int i = 0; i < 10000; i++) {
                    int key = random.nextInt(2000);
                    if (random.nextBoolean()) {
                        concurrentCache.put(key, "value-" + key + "-" + id);
                    } else {
                        concurrentCache.get(key);
                    }
                }
            }, executor);
        }

        CompletableFuture.allOf(futures).join();

        long endTime = System.nanoTime();
        double duration = (endTime - startTime) / 1e9;

        System.out.printf("Concurrent test completed in %.2f seconds%n", duration);
        System.out.println("Final cache size: " + concurrentCache.size());

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // Memory usage statistics
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();  // Suggest garbage collection
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        System.out.printf("Memory usage: %.2f MB%n", usedMemory / (1024.0 * 1024.0));
    }
}
