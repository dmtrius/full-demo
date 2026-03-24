package com.example.demo.apps;

import java.util.HashMap;
import java.util.Map;

public class App42 {
    void main() {

    }

    private static final int MAX_KEY_SIZE = 20;
    private Map<String, String> storage = new HashMap<>();

    private String kvStorage(String key, String secret) {
        if (key.length() > MAX_KEY_SIZE) {
            throw new RuntimeException("Max key length is " + MAX_KEY_SIZE);
        }

        if (storage.containsKey(key)) {
            throw new RuntimeException("");
        }

        return key;
    }
}
