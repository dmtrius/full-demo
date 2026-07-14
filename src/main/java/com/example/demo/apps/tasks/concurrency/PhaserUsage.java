package com.example.demo.apps.tasks.concurrency;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.*;

@SuppressWarnings("java:S2142")
@Slf4j
public class PhaserUsage implements Callable<String> {
    private static final int THREAD_POOL_SIZE = 10;
    private final Phaser phaser;

    private PhaserUsage(Phaser phaser) {
        this.phaser = phaser;
    }

    static void main() {
        try (ExecutorService execService = Executors.newFixedThreadPool(THREAD_POOL_SIZE)) {
            CompletionService<String> completionService
                = new ExecutorCompletionService<>(execService);
            Phaser mainPhaser = new Phaser(0); // Register main thread

            for (int i = 0; i < THREAD_POOL_SIZE; i++) {
                completionService.submit(new PhaserUsage(mainPhaser));
            }

            execService.shutdown();

            try {
                while (!execService.isTerminated()) {
                    String result = completionService.take().get(3, TimeUnit.SECONDS);
                    IO.println(result);
                }
            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                log.error("Exception: {}", e.getMessage(), e);
            }
        }
    }

    private static final Random random = new Random();

    @Override
    public String call() {
        String threadName = Thread.currentThread().getName();
        phaser.register(); // Register this thread
        IO.println("%s registered".formatted(threadName));

        phaser.arriveAndAwaitAdvance(); // Wait for all threads to register

        // Perform some computation
        int a = 0;
        int b = 1;
        for (int i = 0; i < random.nextInt(10_000_000); i++) {
            a = a + b;
            b = a - b;
        }

        IO.println("%s de-registering".formatted(threadName));
        phaser.arriveAndDeregister(); // Signal completion and deregister

        return "%s results: a = %s, b = %s".formatted(threadName, a, b);
    }
}
