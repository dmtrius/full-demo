package com.example.demo.apps;

import org.jspecify.annotations.NonNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static java.lang.IO.println;

public class App34 {
    void main() {
        find1stNunDuplicate("aabbccdhdeeeff")
                .ifPresent(c -> println("First non-duplicate: " + c));
    }

    public static Optional<Character> find1stNunDuplicate(@NonNull String str) {
        Map<Character, Integer> result = new LinkedHashMap<>();
        for (char c : str.toCharArray()) {
            result.merge(c, 1, Integer::sum);
        }
        return result.entrySet().stream()
                .filter(es -> es.getValue().equals(1))
                .findFirst()
                .map(Map.Entry::getKey);
    }
}
