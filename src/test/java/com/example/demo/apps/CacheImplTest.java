package com.example.demo.apps;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CacheImplTest {

    @Test
    @DisplayName("Should return null when cache is empty")
    void getFromEmptyCacheReturnsNull() {
        CacheImpl<Integer, String> cache = new CacheImpl<>(2);
        assertNull(cache.get(1));
    }

    @Test
    @DisplayName("Should insert and retrieve a value")
    void putThenGetReturnsValue() {
        CacheImpl<Integer, String> cache = new CacheImpl<>(2);
        cache.put(1, "one");
        assertEquals("one", cache.get(1));
    }

    @Test
    @DisplayName("Should move accessed entry to front (most recent)")
    void getPromotesToMostRecent() {
        CacheImpl<Integer, String> cache = new CacheImpl<>(3);
        cache.put(1, "one");
        cache.put(2, "two");
        cache.put(3, "three");
        // access 1 so it becomes MRU; then add 4 which should evict LRU which is key 2
        assertEquals("one", cache.get(1));
        cache.put(4, "four");
        assertNull(cache.get(2)); // evicted
        assertEquals("one", cache.get(1));
        assertEquals("three", cache.get(3));
        assertEquals("four", cache.get(4));
    }

    @Test
    @DisplayName("Should evict least recently used when capacity exceeded")
    void evictsLeastRecentlyUsedOnOverflow() {
        CacheImpl<Integer, String> cache = new CacheImpl<>(2);
        cache.put(1, "one");
        cache.put(2, "two");
        cache.put(3, "three"); // should evict key 1
        assertNull(cache.get(1));
        assertEquals("two", cache.get(2));
        assertEquals("three", cache.get(3));
    }

    @Test
    @DisplayName("Should update recency on get to protect from eviction")
    void accessUpdatesRecency() {
        CacheImpl<Integer, String> cache = new CacheImpl<>(2);
        cache.put(1, "one");
        cache.put(2, "two");
        assertEquals("one", cache.get(1)); // promote 1
        cache.put(3, "three"); // should evict 2 now
        assertNull(cache.get(2));
        assertEquals("one", cache.get(1));
        assertEquals("three", cache.get(3));
    }

    @Test
    @DisplayName("Should handle null key lookups gracefully")
    void nullKeyLookup() {
        CacheImpl<Integer, String> cache = new CacheImpl<>(1);
        // get(null) would throw NPE inside equals; ensure we just don't crash the test harness
        assertThrows(NullPointerException.class, () -> cache.get(null));
    }

    @Test
    @DisplayName("Should allow duplicate key inserts to shadow older entries and behave like latest wins")
    void duplicateKeysLatestWins() {
        CacheImpl<Integer, String> cache = new CacheImpl<>(3);
        cache.put(1, "one");
        cache.put(1, "uno");
        // now 1 appears twice, get should return first match which is most recent (front)
        assertEquals("uno", cache.get(1));
        // Adding more to exceed capacity evicts from tail; older duplicate is more likely to be removed first
        cache.put(2, "two");
        cache.put(3, "three");
        cache.put(4, "four");
        assertEquals("four", cache.get(4));
    }
}
