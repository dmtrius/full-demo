package com.example.demo.apps;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static java.lang.IO.println;

public class App34 {
    void main() {
        println(find1stNunDuplicate("aabbccddeeef"));
    }

    Character find1stNunDuplicate(String str) {
        if (Objects.isNull(str) || str.isEmpty()) {
            return null;
        }
        Map<Character, Integer> result = new LinkedHashMap<>();
        for (int i = 0; i < str.length(); ++i) {
            if (!result.containsKey(str.charAt(i))) {
                result.put(str.charAt(i), 1);
            } else {
                result.put(str.charAt(i), result.get(str.charAt(i)) + 1);
            }
        }
        return result.entrySet().stream()
                .filter(es -> es.getValue().equals(1))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(null);
    }
}
