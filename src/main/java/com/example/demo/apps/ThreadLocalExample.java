package com.example.demo.apps;

public class ThreadLocalExample {
    private static final ThreadLocal<Integer> threadLocal = ThreadLocal.withInitial(() -> 1);

    public static void main(String[] args) {
        Runnable task = () -> {
            System.out.println("Initial Value: " + threadLocal.get());
            threadLocal.set(threadLocal.get() + 1);
            System.out.println("Updated Value: " + threadLocal.get());
        };
        Thread thread1 = new Thread(task);
        Thread thread2 = new Thread(task);
        thread1.start();
        thread2.start();
    }
}
