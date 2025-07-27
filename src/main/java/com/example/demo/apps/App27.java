package com.example.demo.apps;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import static java.io.IO.println;

public class App27 {
    void main() {
//        ft1();
//        ft2();
//        parallel();
        streams();
    }

    @SuppressWarnings("preview")
    void streams() {
        // Create an IntStream from 1 to 5 (inclusive)
        IntStream intStream = IntStream.rangeClosed(1, 5);
        // Print the elements of the IntStream
        intStream.forEach(System.out::println);
        println("-----");
        // random stream
        SecureRandom random = new SecureRandom();
        IntStream.generate(() -> random.nextInt(1, 42))
                .limit(10)
                .forEach(System.out::println);
    }

    @SuppressWarnings({"preview", "unused"})
    void parallel() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        // Sequential
        println("Sequential Stream:");
        numbers.forEach(System.out::println);
        // Sequential Stream
        println("Sequential Stream:");
        numbers.stream().forEach(System.out::println);
        // Sequential Parallel Stream
        println("Sequential Parallel Stream:");
        numbers.stream().parallel().forEach(System.out::println);
        // Parallel Stream
        println("Parallel Stream:");
        numbers.parallelStream().forEach(System.out::println);
    }

    @SuppressWarnings("unused")
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

    @SuppressWarnings("unused")
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