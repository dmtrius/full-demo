package com.example.demo.apps.tasks.concurrency;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("java:S2142")
@Slf4j
public class CyclicBarrierExample {
    void main() {
        final int numThreads = 7;
        CyclicBarrier barrier = new CyclicBarrier(numThreads,
                () -> IO.println("All threads reached the barrier!"));
        for (int i = 0; i < numThreads; i++) {
            new Thread(new Worker(barrier)).start();
        }
    }

    static class Worker implements Runnable {
        private final CyclicBarrier barrier;
        private static final Random rand = new Random();

        Worker(CyclicBarrier barrier) {
            this.barrier = barrier;
        }

        @Override
        public void run() {
            final long duration = rand.nextLong(2000);
            IO.println(
                    duration + " : " +
                    Thread.currentThread().getName() + " is working...");
            try {
                TimeUnit.MILLISECONDS.sleep(duration); // Simulating work
                IO.println(Thread.currentThread().getName()
                        + " reached the barrier");
                barrier.await(); // Wait at the barrier
                IO.println(
                        duration + " : " +
                        Thread.currentThread().getName()
                        + " passed the barrier!");
            } catch (InterruptedException | BrokenBarrierException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
