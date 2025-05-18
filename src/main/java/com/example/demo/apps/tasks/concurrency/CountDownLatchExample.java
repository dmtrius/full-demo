package com.example.demo.apps.tasks.concurrency;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class CountDownLatchExample {
    public static void main(String[] args) {
        final int numWorkers = 7;
        CountDownLatch latch = new CountDownLatch(numWorkers);
        for (int i = 0; i < numWorkers; i++) {
            new Thread(new Worker(latch)).start();
        }
        try {
            latch.await(); // Wait for all workers to finish
            System.out.println("All workers finished. Main thread proceeds.");
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }
    static class Worker implements Runnable {
        private final CountDownLatch latch;
        Worker(CountDownLatch latch) {
            this.latch = latch;
        }
        @Override
        public void run() {
            final long duration = new Random().nextLong(2000);
            System.out.println(
                    duration + " : " +
                    Thread.currentThread().getName() + " is working...");
            try {
                Thread.sleep(duration); // Simulating work
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
            System.out.println(
                    duration + " : " +
                    Thread.currentThread().getName() + " finished work.");
            latch.countDown(); // Decrease the latch count
        }
    }
}
