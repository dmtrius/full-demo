package com.example.demo.apps;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class ProducerConsumer {

    public static void main(String[] args) {
        System.out.println(String.format("%32s", Integer.toBinaryString(3).replace(' ', '0')));
        System.out.println(Integer.toBinaryString(-3));



        log.info("How to use wait and notify method in Java");
        log.info("Solving Producer Consumer Problem");

        BlockingQueue<Integer> buffer = new LinkedBlockingQueue<>();
        int maxSize = 10;

        Thread producer = new Producer(buffer, maxSize, "PRODUCER");
//        Thread producer2 = new Producer(buffer, maxSize, "PRODUCER2", 10);
        Thread consumer = new Consumer(buffer,"CONSUMER");

        producer.setUncaughtExceptionHandler(getUncaughtExceptionHandler());
        consumer.setUncaughtExceptionHandler(getUncaughtExceptionHandler());

//        producer.start();
//        producer2.start();
//        consumer.start();
    }
    private static Thread.UncaughtExceptionHandler getUncaughtExceptionHandler() {
        return (t, e) -> {
            log.info("[UEH] Thread Name: {}", t.getName());
            log.info("[UEH] Exception: {}", e.getLocalizedMessage());
        };
    }

    public static final String RED = "\033[31m";
    public static final String GREEN = "\033[32m";
    public static final String RESET = "\033[0m";
}

@Slf4j
class Producer extends Thread {
    private final BlockingQueue<Integer> queue;
    private final int maxSize;
    private long waitingTime;
    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    public Producer(BlockingQueue<Integer> queue, int maxSize, String name, long waitingTime) {
        super(name);
        this.queue = queue;
        this.maxSize = maxSize;
        this.waitingTime = waitingTime;
    }
    public Producer(BlockingQueue<Integer> queue, int maxSize, String name) {
        super(name);
        this.queue = queue;
        this.maxSize = maxSize;
    }

//    @SneakyThrows
    @Override
    public void run() {
        while (true) {
            synchronized (queue) {
                while (queue.size() == maxSize) {
                    try {
                        log.info("{}Queue is full{}, Producer thread waiting for Consumer to take something from queue", ProducerConsumer.RED, ProducerConsumer.RESET);
                        queue.wait();
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }

                int i = random.nextInt();
                log.info("Producing value : {}", i);
                queue.offer(i);
                queue.notifyAll();
            }
            try {
                Thread.sleep(waitingTime != 0 ? waitingTime : 100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

@Slf4j
class Consumer extends Thread {
    private final BlockingQueue<Integer> queue;

    public Consumer(BlockingQueue<Integer> queue, String name) {
        super(name);
        this.queue = queue;
    }

//    @SneakyThrows
    @Override
    public void run() {
        while (true) {
            synchronized (queue) {
                while (queue.isEmpty()) {
                    log.info("{}Queue is empty{}, Consumer thread is waiting for Producer thread to put something in queue", ProducerConsumer.GREEN, ProducerConsumer.RESET);
                    try {
                        queue.wait();
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                try {
                    log.info("Consuming value : {}", queue.take());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                queue.notifyAll();
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
