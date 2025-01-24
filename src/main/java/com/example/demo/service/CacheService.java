package com.example.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CacheService {
    // Evict all entries from the "client" cache
    @CacheEvict(value = "client", allEntries = true)
    public void clearClientsCache() {
        log.info("Clearing all entries from the 'user' cache.");
    }
    // Evict all entries from the "tx" cache
    @CacheEvict(value = "tx", allEntries = true)
    public void clearTxsCache() {
        log.info("Clearing all entries from the 'tx' cache.");
    }
}
