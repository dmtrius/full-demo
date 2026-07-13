package com.example.demo.apps;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class ReentrantLockHowTo {
    private final ReentrantLock lock = new ReentrantLock();
    private int count = 0;

    //Locking using Lock and ReentrantLock
    public int getCount() {
        lock.lock();
        try {
            IO.println(Thread.currentThread().getName() + " gets Count: " + count);
            return count++;
        } finally {
            lock.unlock();
        }
    }

    //Implicit locking using synchronized keyword
    public synchronized int getCountTwo() {
        return count++;
    }

    void main() {
        final ReentrantLockHowTo counter = new ReentrantLockHowTo();
        Thread t1 = new Thread(() -> {
            while (counter.getCount() < 6) {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException _) {
                    //ignore
                }
            }
        });

        Thread t2 = new Thread(() -> {
            while (counter.getCountTwo() < 6) {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException _) {
                    //ignore
                }
            }
        });

        t1.start();
        t2.start();

    }
}
