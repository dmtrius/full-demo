package com.example.demo.apps.update;

import java.util.concurrent.BlockingQueue;

public class Consumer implements Runnable {
    private BlockingQueue<Integer> queue;

    public Consumer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        int value = -1;
        while (true) {
            try {
                value = queue.take();
                System.out.println("Consumed: " + value);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Stop the consumer thread when the producer adds -1 to the queue
            if (value == -1)
                break;
        }
    }
}
