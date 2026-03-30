package com.example.demo.apps;

import java.util.concurrent.StructuredTaskScope;

import static java.lang.IO.println;

@SuppressWarnings("preview")
public class StructuredConcurrencyExample {
    void main() {
        try (var scope = StructuredTaskScope.open()) {
            var result1 = scope.fork(() -> {
                if (Math.random() > 0.6)  {
                    throw new SCException("Task 1 failed");
                }
                return "Task 1 completed";
            });

            var result2 = scope.fork(() -> "Task 2 completed");

            scope.join();

            println(result1.get());
            println(result2.get());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            println("Error in one of the tasks: " + e.getMessage());
        }
    }
}
