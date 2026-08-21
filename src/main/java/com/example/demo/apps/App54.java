package com.example.demo.apps;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class App54 {
    void main() {
        java.util.function.Consumer<String> print = IO::println;
        java.util.function.Consumer<String> logger = log::info;
        java.util.function.Consumer<String> printAndLog = print.andThen(logger);
        List.of("Hello", "World", "Java", "Lambda").forEach(printAndLog);
    }
}
