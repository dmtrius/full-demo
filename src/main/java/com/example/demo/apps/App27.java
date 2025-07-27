package com.example.demo.apps;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.io.IO.println;

public class App27 {
    void main() {
        ft1();
        ft2();
        parallel();
    }

    @SuppressWarnings("preview")
    void parallel() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // Sequential
        println("Sequential Stream:");
        numbers.forEach(System.out::println);

        // Sequential Stream
        println("Sequential Stream:");
        numbers.stream().forEach(System.out::println);

        // Parallel Stream
        println("Parallel Stream:");
        numbers.parallelStream().forEach(System.out::println);
    }

    void ft2() {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            throw new CustomException("Custom exception occurred");
        });
        CompletableFuture<Integer> handledFuture = future.exceptionally(ex -> {
            if (ex instanceof CustomException) {
                System.err.println("Custom Exception: " + ex.getMessage());
                return -1;
            } else {
                System.err.println("Generic Exception: " + ex.getMessage());
                return -2;
            }
        });
        handledFuture.thenAccept(result -> System.out.println("Result: " + result));
    }

    void ft1() {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            throw new RuntimeException("Something went wrong");
        });
        CompletableFuture<Integer> handledFuture = future
                .exceptionally(ex -> {
                    System.err.println("Exception: " + ex.getMessage());
                    return -1;
                })
                .thenApply(result -> result * 2);
        handledFuture.thenAccept(result -> System.out.println("Result: " + result));
    }
}

class CustomException extends RuntimeException {
    public CustomException(String customExceptionOccurred) {
        super(customExceptionOccurred);
    }
}