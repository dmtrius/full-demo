package com.example.demo.apps;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static java.lang.IO.println;

public class App33 {
    void main() {
        CacheImpl<Integer, String> cacheInstance = new CacheImpl<>(3);
        cacheInstance.put(1, "one");
        cacheInstance.put(3, "three");
        cacheInstance.put(2, "two");
        println(cacheInstance.get(2));
        cacheInstance.put(4, "four");
        println(cacheInstance.get(4));
    }
}

class CacheImpl<K, V> {
    private final int SIZE;
    private final List<Entry<K, V>> cache = new LinkedList<>();

    public CacheImpl(int size) {
        this.SIZE = size;
    }

    public void put(K key, V value) {
        if (cache.size() >= SIZE) {
            cache.removeLast();
        }
        Entry<K, V> _value = getValue(key);
        if (!Objects.isNull(_value)) {
            cache.remove(_value);
        }
        cache.addFirst(new Entry<>(key, value));
    }

    public V get(K key) {
        if (Objects.isNull(key)) {
            throw new NullPointerException();
        }
        Entry<K, V> value = getValue(key);
        if (Objects.isNull(value)) {
            return null;
        }
        cache.remove(value);
        cache.addFirst(value);
        return value.value();
    }

    private Entry<K, V> getValue(K key) {
        return cache
                .stream()
                .filter(e -> e.key().equals(key)).findFirst()
                .orElse(null);
    }
}

record Entry<K, V> (K key, V value) {}
