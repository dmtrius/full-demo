package com.example.demo.apps.tasks.concurrency;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@Slf4j
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
                CompletableFuture.runAsync(() -> IO.println("Task 1")),
                CompletableFuture.runAsync(() -> IO.println("Task 2")),
                CompletableFuture.runAsync(() -> IO.println("Task 3"))
        );
        allFutures.join(); // Waits for all tasks to complete
        IO.println("All tasks finished!");
    }

    void test4() {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            if (Math.random() > 0.5) {
                throw new CFException("Something went wrong!");
            }
            return 42;
        });
        future.exceptionally(ex -> {
            log.error("Error: {}", ex.getMessage(), ex);
            return -1; // Default fallback value
        }).thenAccept(IO::println);
    }

    void test3() {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "Hello");
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> "World");
        future1.thenCombine(future2, (res1, res2) -> res1 + " " + res2)
                .thenAccept(IO::println);
    }

    void test2() {
        CompletableFuture.supplyAsync(() -> "Java")
                .thenApply(str -> str + " Future")  // Transforms result
                .thenApply(String::toUpperCase)    // Converts to uppercase
                .thenAccept(IO::println);  // Consumes result
    }

    void test1() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error("Interrupted: {}", e.getMessage(), e);
                Thread.currentThread().interrupt();
                throw new CFException("Task interrupted");
            }
            return "Hello, World!";
        });
        future.thenAccept(result -> IO.println("Result: " + result));
        // Keep the main thread alive to see async output (only needed in standalone Java applications)
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            log.error("Interrupted: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
            throw new CFException("Main thread interrupted");
        }
    }
}

class CFException extends RuntimeException {
    public CFException(String s) {
        super(s);
    }
}
