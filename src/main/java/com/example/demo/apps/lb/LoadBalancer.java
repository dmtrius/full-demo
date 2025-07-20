package com.example.demo.apps.lb;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class LoadBalancer {
    private final static int SIZE = 10;
    private final ConcurrentHashMap<Integer, String> lb = new ConcurrentHashMap<>();
    private final static Random uuid = new Random();
    private volatile int position = 0;
    private final Map<Integer, Integer> current = new HashMap<>();

    public synchronized String getServer() {
        if (size() == 0) {
            throw new RuntimeException();
        }
        if (position > SIZE) {
            position = 0;
        }
        return lb.get(position++);
    }

    public int size() {
        return lb.size();
    }

    private boolean isExist(String server) {
        return lb.entrySet().stream().anyMatch((v) -> v.getValue().equals(server));
    }

    public synchronized void register(String server) {
        if (size() < SIZE && !isExist(server)) {
            Integer _uuid = uuid.nextInt();
            lb.put(_uuid, server);
            current.put(position++, _uuid);
        } else {
            throw new RuntimeException();
        }
    }
}
