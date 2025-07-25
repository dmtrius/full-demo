package com.example.demo.apps;

import java.util.List;

import static java.io.IO.println;

public class App27 {
    void main() {
        var list = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        var item = list.stream().
                filter(i -> i > 11)
                .findFirst();
        println(item.orElse(-1));
    }
}
