package com.example.demo.controller;

import com.example.demo.service.CacheService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cache")
public class CacheController {

    private final CacheService service;

    public CacheController(CacheService service) {
        this.service = service;
    }

    @GetMapping("/clear-clients-cache")
    public String clearUserCache() {
        service.clearClientsCache();
        return "Clients cache cleared successfully!";
    }

    @GetMapping("/clear-tx-cache")
    public String clearTxCache() {
        service.clearTxsCache();
        return "TXs cache cleared successfully!";
    }
}
