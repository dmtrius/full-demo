package com.example.demo.apps;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class App8 {
    public static void main(String... args) {
        testTimes();
    }

    private static void testTimes() {
        // Max value for LocalDate
        LocalDate maxLocalDate = LocalDate.MAX;
        System.out.println("Max LocalDate: " + maxLocalDate);

        // Max value for LocalDateTime
        LocalDateTime maxLocalDateTime = LocalDateTime.MAX;
        System.out.println("Max LocalDateTime: " + maxLocalDateTime);

        // Max value for Instant (as string because it's too far in the future for human readability)
        Instant maxInstant = Instant.MAX;
        System.out.println("Max Instant: " + maxInstant.toString());
    }

    private static void testCounter() {
        SyncCounter counter = new SyncCounter();
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                counter.increment();
            }
            System.out.println(counter.getCount());
        });
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                counter.increment();
            }
            System.out.println(counter.getCount());
        });
        thread1.start();
        thread2.start();
    }

    private static void testCounter2() {
        SyncCounter2 counter = new SyncCounter2();
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                counter.increment();
            }
            log.info("{}", counter.getCount());
        });
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                counter.increment();
            }
            log.info("{}", counter.getCount());
        });
        thread1.start();
        thread2.start();
    }
}

class SyncCounter {
    private volatile long count = 0;

    public synchronized void increment() {
        this.count++;
    }
    public synchronized long getCount() {
        return this.count;
    }
}

class SyncCounter2 {
    private final AtomicLong count = new AtomicLong(0);

    public void increment() {
        count.incrementAndGet();
    }
    public void decrement() {
        count.decrementAndGet();
    }
    public long getCount() {
        return count.get();
    }
}

record Cart(int id, String name) {
    public record Item(int id, String name, int quantity) {
        public Item withQuantity(int quantity) {
            return new Item(id, name, quantity);
        }
    }
}
