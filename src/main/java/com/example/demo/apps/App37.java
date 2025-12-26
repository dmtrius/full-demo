package com.example.demo.apps;

import java.util.List;

import static java.lang.IO.println;

public class App37 {
    void main() {
        m1();
    }

    private static void m2() {

    }

    private static void m1() {
        List<Integer> l = List.of(1, 2, 3, 4, 5, 6, 7);
        List<Integer> r = l.stream()
                .filter(i -> i % 2 == 0)
                .toList()
                .stream()
                .filter(i -> i > 4)
                .toList();
        println(r);
    }
}
