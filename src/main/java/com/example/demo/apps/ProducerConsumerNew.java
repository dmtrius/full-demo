package com.example.demo.apps;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ProducerConsumerNew {
    private static final int MAX_SIZE = 10;
    private static final BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(MAX_SIZE);

    void main() {
        Thread producerThread = new Thread(() -> {
            try {
                while (true) {
                    int item = produce();
                    queue.put(item);
                    log.info("Produced: {}", item);
                    if (queue.size() == MAX_SIZE) {
                        log.info("producer sleeps...");
                        TimeUnit.SECONDS.sleep(3);
                    }
                }
            } catch (InterruptedException e) {
                log.error("Interrupted PRODUCER");
            }
        });

        Thread consumerThread = new Thread(() -> {
            try {
                while (true) {
                    int item = queue.take();
                    consume(item);
                    log.info("Consumed: {}", item);
                    if (queue.isEmpty()) {
                        log.info("consumer sleeps...");
                        TimeUnit.MILLISECONDS.sleep(100);
                    }
                }
            } catch (InterruptedException e) {
                log.error("Interrupted CONSUMER");
            }
        });

        producerThread.start();
        consumerThread.start();
    }
    private static final Random rand = new Random();
    private static int produce() {
        return rand.nextInt();
    }

    private static void consume(int item) {
        log.info("Consuming: {}", item);
    }
}
