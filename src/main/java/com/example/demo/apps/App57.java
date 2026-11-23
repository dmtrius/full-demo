package com.example.demo.apps;

import com.google.common.hash.Hashing;
import lombok.extern.log4j.Log4j2;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Log4j2
public class App57 {
    @SuppressWarnings("UnstableApiUsage")
    void main() {
        var str = "Hello World";
        log.info(str);
        byte[] hash128 = Hashing.murmur3_128(Integer.MAX_VALUE)
                .hashString(str, StandardCharsets.UTF_8).asBytes();
        log.info(Arrays.toString(hash128));
        var list = Arrays.asList("one", "two", "three");
        Objects.checkIndex(1, list.size());
        m1();
    }

    @SuppressWarnings("all")
    void m1() {
        log.info("----------");
        List<String> names = List.of("Alice", "Bob", "Charlie", "Diana", "Eve");
        long count;
        var list = names.stream()
                .map(
                        name -> {
                            log.info("Processing name [toList]: {}", name);
                            return name.toUpperCase();
                        }
                ).toList();
        count = names.stream()
                .map(
                        name -> {
                            log.info("Processing name [to count]: {}", name);
                            return name.toUpperCase();
                        }
                )
                .count();
        log.info(count);
        log.info(list);
    }
}
