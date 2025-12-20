package com.example.demo.apps;

//import java.util.concurrent.StructuredTaskScope;
import java.util.function.Supplier;

public class StructuredConcurrencyExample {
    void main() {
        /*try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            Supplier<String> result1 = scope.fork(() -> {
                if (Math.random() > 0.6)  {
                    throw new RuntimeException("Task 1 failed");
                }
                return "Task 1 completed";
            });

            Supplier<String> result2 = scope.fork(() -> "Task 2 completed");

            scope.join();
            scope.throwIfFailed();

            System.out.println(result1.get());
            System.out.println(result2.get());

        } catch (Exception e) {
            System.out.println("Error in one of the tasks: " + e.getMessage());
        }*/
    }
}
