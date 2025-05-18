package com.example.demo.apps.tasks.concurrency;

import java.util.concurrent.CompletableFuture;

public class CompletableFutureExample {
    void main() {
        test1();
        test2();
        test3();
        test4();
        test5();
    }

    void test5() {
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                CompletableFuture.runAsync(() -> System.out.println("Task 1")),
                CompletableFuture.runAsync(() -> System.out.println("Task 2")),
                CompletableFuture.runAsync(() -> System.out.println("Task 3"))
        );
        allFutures.join(); // Waits for all tasks to complete
        System.out.println("All tasks finished!");
    }

    void test4() {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            if (Math.random() > 0.5) {
                throw new RuntimeException("Something went wrong!");
            }
            return 42;
        });
        future.exceptionally(ex -> {
            System.out.println("Error: " + ex.getMessage());
            return -1; // Default fallback value
        }).thenAccept(System.out::println);
    }

    void test3() {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "Hello");
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> "World");
        future1.thenCombine(future2, (res1, res2) -> res1 + " " + res2)
                .thenAccept(System.out::println);
    }

    void test2() {
        CompletableFuture.supplyAsync(() -> "Java")
                .thenApply(str -> str + " Future")  // Transforms result
                .thenApply(String::toUpperCase)    // Converts to uppercase
                .thenAccept(System.out::println);  // Consumes result
    }

    void test1() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Hello, World!";
        });
        future.thenAccept(result -> System.out.println("Result: " + result));
        // Keep the main thread alive to see async output (only needed in standalone Java applications)
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
