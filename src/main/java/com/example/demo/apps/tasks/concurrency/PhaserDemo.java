package com.example.demo.apps.tasks.concurrency;

import java.util.concurrent.Phaser;

public class PhaserDemo {
    public static void main(String[] args) {
        Phaser phaser = new Phaser(1); // Register main thread
        System.out.println("Starting");

        // Create and start 3 threads
        new MyThread(phaser, "A");
        new MyThread(phaser, "B");
        new MyThread(phaser, "C");

        // Wait for all threads to complete phase 0
        int curPhase = phaser.getPhase();
        phaser.arriveAndAwaitAdvance();
        System.out.println("Phase " + curPhase + " Complete");

        // Wait for all threads to complete phase 1
        curPhase = phaser.getPhase();
        phaser.arriveAndAwaitAdvance();
        System.out.println("Phase " + curPhase + " Complete");

        // Wait for all threads to complete phase 2
        curPhase = phaser.getPhase();
        phaser.arriveAndAwaitAdvance();
        System.out.println("Phase " + curPhase + " Complete");

        // Deregister main thread
        phaser.arriveAndDeregister();

        if (phaser.isTerminated()) {
            System.out.println("The Phaser is terminated");
        }
    }
}

class MyThread implements Runnable {
    private final Phaser phaser;
    private final String name;

    MyThread(Phaser p, String n) {
        phaser = p;
        name = n;
        phaser.register(); // Register this thread
        new Thread(this).start();
    }

    public void run() {
        while (!phaser.isTerminated()) {
            System.out.println(name + " is working in phase " + phaser.getPhase());
            phaser.arriveAndAwaitAdvance(); // Wait for others
        }
    }
}
