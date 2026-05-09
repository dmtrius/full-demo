package com.example.demo.apps.tasks;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class ProductCacheService {
    private final Cache<Long, Product> productCache;

    public ProductCacheService(Cache<Long, Product> productCache) {
        this.productCache = productCache;
    }

    /**
     * Get product from cache
     */
    public Optional<Product> getProduct(Long id) {
        return Optional.ofNullable(productCache.getIfPresent(id));
    }

    /**
     * Put product into cache
     */
    public void putProduct(Long id, Product product) {
        productCache.put(id, product);
    }

    /**
     * Invalidate a specific product (your original snippet)
     */
    public void invalidateProduct(Long id) {
        productCache.invalidate(id);
    }

    /**
     * Invalidate all cache entries
     */
    public void invalidateAll() {
        productCache.invalidateAll();
    }

    /**
     * Monitoring (expose via actuator or custom metrics)
     */
    public CacheStats getCacheStats() {
        return productCache.stats();
    }

    Cache<Long, Product> productCache() {
        return Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .recordStats() // required for CacheStats
                .build();
    }

    record Product(Long id, String name){}
}


