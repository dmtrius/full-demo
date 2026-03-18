package com.example.demo.apps;

import module java.base;

import static java.lang.IO.println;

public class Deduplication {
    void main() {
        List<Integer> listWithDuplicates = List.of(1, 7, 2, 2, 6, 1, 4, 2, 1, 6, 7);
        List<Integer> deduplicatedList = deduplicate(listWithDuplicates);
        println(deduplicatedList);
    }

    private static List<Integer> deduplicate(List<Integer> list) {
        List<Integer> result = new LinkedList<>();
        AtomicInteger count = new AtomicInteger();
        list.forEach(i -> {
            if (!result.contains(i)) {
                result.add(i);
            } else {
                count.incrementAndGet();
            }
        });
        println("COUNT: %d".formatted(count.get()));
        return result;
    }
}
