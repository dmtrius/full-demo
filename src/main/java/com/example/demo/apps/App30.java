package com.example.demo.apps;

import lombok.SneakyThrows;

import static java.io.IO.println;

public class App30 {
    @SneakyThrows
    void main() {
        println("brrr");
        processInfo();
    }

    void processInfo() {
        ProcessHandle.current().info().command().ifPresent(System.out::println);
    }
}
