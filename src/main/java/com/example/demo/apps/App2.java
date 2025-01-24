package com.example.demo.apps;

import lombok.extern.slf4j.Slf4j;

import java.util.stream.Stream;

@Slf4j
public class App2 {
    public static void main(String... args) {
        printFibonacci(10);
    }

    private static void printFibonacci(int a) {
        Stream.iterate(new long[]{0, 1}, f -> new long[]{f[1], f[0] + f[1]})
                .limit(a)
                .map(n -> n[0])
                .forEach(i -> log.info("{}", i));
    }
}
