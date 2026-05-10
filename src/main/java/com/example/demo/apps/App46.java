package com.example.demo.apps;

import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.UUID;
import java.util.stream.Gatherers;

import static java.lang.IO.println;

public class App46 {
    void main() {
        m1(UUID.randomUUID().toString());
        m2(UUID.randomUUID().toString());
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

    void m2(@NonNull String param) {
        println(param);
        List<Integer> list = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        var windows = list.stream()
            .gather(Gatherers.windowFixed(3))
            .toList();
        windows.forEach(IO::println);
    }
}
