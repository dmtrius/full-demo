package com.example.demo.apps.tasks.concurrency;

import java.util.Random;
import java.util.concurrent.*;

public class PhaserUsage implements Callable<String> {
    private static final int THREAD_POOL_SIZE = 10;
    private final Phaser phaser;

    private PhaserUsage(Phaser phaser) {
        this.phaser = phaser;
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService execService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        CompletionService<String> completionService
                = new ExecutorCompletionService<>(execService);
        Phaser phaser = new Phaser(0); // Register main thread

        for (int i = 0; i < THREAD_POOL_SIZE; i++) {
            completionService.submit(new PhaserUsage(phaser));
        }

        execService.shutdown();

        try {
            while (!execService.isTerminated()) {
                String result = completionService.take().get();
                System.out.println(result);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static final Random random = new Random();

    @Override
    public String call() {
        String threadName = Thread.currentThread().getName();
        phaser.register(); // Register this thread
        System.out.println(threadName + " registered");

        phaser.arriveAndAwaitAdvance(); // Wait for all threads to register

        // Perform some computation
        int a = 0, b = 1;
        for (int i = 0; i < random.nextInt(10_000_000); i++) {
            a = a + b;
            b = a - b;
        }

        System.out.println(threadName + " de-registering");
        phaser.arriveAndDeregister(); // Signal completion and deregister

        return threadName + " results: a = " + a + ", b = " + b;
    }
}
