package com.example.demo.apps.update;

import lombok.SneakyThrows;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ProducerConsumer {
    private static final int MAX_CAPACITY = 5;
    private final BlockingQueue<Integer> queue;
    private final Thread producerThread;
    private final Thread consumerThread;

    public ProducerConsumer() {
        queue = new LinkedBlockingQueue<>(MAX_CAPACITY);
        producerThread = new Thread(new Producer(queue));
        consumerThread = new Thread(new Consumer(queue));

        // Start the threads
        producerThread.start();
        consumerThread.start();
    }
//    @SneakyThrows
    public void shutdown() throws InterruptedException {
        // Add a signal to stop the producer thread
        queue.put(-1);

        // Wait for the producer thread to finish
        try {
            producerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Wait for the consumer thread to finish
        try {
            consumerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ProducerConsumer pc = new ProducerConsumer();
        // Keep the program running for 5 seconds
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        pc.shutdown();
    }
}
