package com.example.demo.apps.tasks.concurrency;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

@SuppressWarnings("java:S2142")
@Slf4j
public class CountDownLatchExample {
    void main() {
        final int numWorkers = 7;
        CountDownLatch latch = new CountDownLatch(numWorkers);
        for (int i = 0; i < numWorkers; i++) {
            new Thread(new Worker(latch)).start();
        }
        try {
            latch.await(); // Wait for all workers to finish
            IO.println("All workers finished. Main thread proceeds.");
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new CDLException("CDL interrupted", e);
        }
    }

    static class Worker implements Runnable {
        private final CountDownLatch latch;
        private static final Random rand = new Random();
        private static final int MAX_DURATION = 2000;

        Worker(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void run() {
            final long duration = rand.nextLong(MAX_DURATION);
            IO.println(
                    duration + " : " +
                    Thread.currentThread().getName() + " is working...");
            try {
                Thread.sleep(duration); // Simulating work
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
            IO.println(
                    duration + " : " +
                    Thread.currentThread().getName() + " finished work.");
            latch.countDown(); // Decrease the latch count
        }
    }
}

class CDLException extends RuntimeException {
    public CDLException(String message, InterruptedException e) {
        super(message, e);
    }
}
