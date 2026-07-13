package com.example.demo.apps;

public class ThreadLocalExample {
    private static final ThreadLocal<Integer> threadLocal = ThreadLocal.withInitial(() -> 1);

    void main() {
        try {
            Runnable task = () -> {
                IO.println("Initial Value: " + threadLocal.get());
                threadLocal.set(threadLocal.get() + 1);
                IO.println("Updated Value: " + threadLocal.get());
            };
            Thread thread1 = new Thread(task);
            Thread thread2 = new Thread(task);
            thread1.start();
            thread2.start();
        } finally {
            threadLocal.remove();
        }
    }
}
