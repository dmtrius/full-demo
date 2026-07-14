package com.example.demo.apps.update;

import java.util.concurrent.BlockingQueue;

public class Producer implements Runnable {
    private final BlockingQueue<Integer> queue;
    public Producer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        int i = 1;
        while (true) {
            try {
                queue.put(i);
                IO.println("Produced: " + i);
            } catch (InterruptedException _) {
                Thread.currentThread().interrupt();
            }
            i++;
        }
    }
}
