package com.example.demo.apps;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@Slf4j
public class App5 {

    void main() {
        testVirtualThreads();
    }

//    @SneakyThrows
    private static void testVirtualThreads() {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

            for (int i = 0; i < 100; i++) {
                executor.submit(() -> {
                    try {
                        Thread.sleep(1000); // Simulate some work
                        log.info("Task completed: {}", Thread.currentThread().getName());
                    } catch (InterruptedException e) {
                        //ignore
                    }
                });
            }

            executor.shutdown();
            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException _) {

            }
        }
    }

}
