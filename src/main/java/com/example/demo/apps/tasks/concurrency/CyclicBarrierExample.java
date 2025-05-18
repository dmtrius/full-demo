package com.example.demo.apps.tasks.concurrency;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CyclicBarrierExample {
    public static void main(String[] args) {
        final int numThreads = 7;
        CyclicBarrier barrier = new CyclicBarrier(numThreads,
                () -> System.out.println("All threads reached the barrier!"));
        for (int i = 0; i < numThreads; i++) {
            new Thread(new Worker(barrier)).start();
        }
    }
    static class Worker implements Runnable {
        private final CyclicBarrier barrier;
        Worker(CyclicBarrier barrier) {
            this.barrier = barrier;
        }
        @Override
        public void run() {
            final long duration = new Random().nextLong(2000);
            System.out.println(
                    duration + " : " +
                    Thread.currentThread().getName() + " is working...");
            try {
                TimeUnit.MILLISECONDS.sleep(duration); // Simulating work
                System.out.println(Thread.currentThread().getName()
                        + " reached the barrier");
                barrier.await(); // Wait at the barrier
                System.out.println(
                        duration + " : " +
                        Thread.currentThread().getName()
                        + " passed the barrier!");
            } catch (InterruptedException | BrokenBarrierException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
