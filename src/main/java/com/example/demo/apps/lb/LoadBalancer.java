package com.example.demo.apps.lb;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class LoadBalancer {
    private static final int SIZE = 10;
    private final ConcurrentHashMap<Integer, String> lb = new ConcurrentHashMap<>();
    private static final Random uuid = new Random();
    private volatile int position = 0;
    @Getter
    private final Map<Integer, Integer> current = new HashMap<>();

    public synchronized String getServer() {
        if (size() == 0) {
            throw new LoadBalancerException();
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
        return lb.entrySet().stream().anyMatch(v -> v.getValue().equals(server));
    }

    @SuppressWarnings("unused")
    public synchronized void unregister(String server) {
        if (size() > 0 && isExist(server)) {
            lb.entrySet().removeIf(v -> v.getValue().equals(server));
            current.entrySet().removeIf(v -> v.getValue().equals(server));
        } else {
            throw new LoadBalancerException();
        }
    }

    public synchronized void register(String server) {
        if (size() < SIZE && !isExist(server)) {
            Integer uuidInt = uuid.nextInt();
            lb.put(uuidInt, server);
            current.put(position++, uuidInt);
        } else {
            throw new LoadBalancerException();
        }
    }
}

class LoadBalancerException extends RuntimeException {}