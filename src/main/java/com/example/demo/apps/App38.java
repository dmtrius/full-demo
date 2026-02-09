package com.example.demo.apps;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.IO.println;

public class App38 {
    void main() {
        String input = "repeating-characters";
        Optional<Character> result = findFirstRepeated(input);
        result.ifPresent(c -> println("First repeated: " + c));
    }

    public static Optional<Character> findFirstRepeated(String str) {
        return str.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.groupingBy(
                        c -> c,
                        LinkedHashMap::new,
                        Collectors.counting()
                ))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .findFirst();
    }
}
