package com.example.demo.apps;

import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.UUID;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

import static java.lang.IO.println;

public class App46 {
    void main() {
        m1(UUID.randomUUID().toString());
        m2();
        m3();
        m4();
        m5();
        Stream.of(1, 2, 3, 4, 5).limit(2).forEach(IO::println);
    }

    /// m1() for practise some code
    ///
    /// Example:
    /// ```java
    /// m1("param");
    /// ```
    ///
    /// @param param the PARAM
    /// @return void
    void m1(@NonNull String param) {
        println(param);
        List<Integer> list = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        var windows = list.stream()
            .gather(Gatherers.windowSliding(3))
            .toList();
        windows.forEach(IO::println);
    }

    void m2() {
        List<Integer> list = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        var windows = list.stream()
            .gather(Gatherers.windowFixed(3))
            .toList();
        windows.forEach(IO::println);
    }

    void m3() {
        List<Integer> list = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        list.stream()
            .gather(Gatherers.scan(() -> 0, Integer::sum))
            .forEach(IO::println);
    }

    void m4() {
        List<String> list = List.of("A", "A", "B", "B", "B", "C");
        var result = list.stream()
            .gather(Gatherers.mapConcurrent(4, String::toLowerCase))
            .toList();
        println(result);
    }

    void m5() {
        List<Integer> list = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9);
        var result = list.stream()
            .gather(Gatherers.fold(() -> 1, (n1, n2) -> n1 * n2))
            .findFirst();
        println(result.orElse(-1));
    }
}
