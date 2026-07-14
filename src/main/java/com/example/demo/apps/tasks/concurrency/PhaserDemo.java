package com.example.demo.apps.tasks.concurrency;

import java.util.concurrent.Phaser;

public class PhaserDemo {

    private static final String PHASE_D_COMPLETE = "Phase %d Complete";

    void main() {
        Phaser phaser = new Phaser(1); // Register main thread
        IO.println("Starting");

        // Create and start 3 threads
        new MyThread(phaser, "A");
        new MyThread(phaser, "B");
        new MyThread(phaser, "C");

        // Wait for all threads to complete phase 0
        int curPhase = phaser.getPhase();
        phaser.arriveAndAwaitAdvance();
        IO.println(PHASE_D_COMPLETE.formatted(curPhase));

        // Wait for all threads to complete phase 1
        curPhase = phaser.getPhase();
        phaser.arriveAndAwaitAdvance();
        IO.println(PHASE_D_COMPLETE.formatted(curPhase));

        // Wait for all threads to complete phase 2
        curPhase = phaser.getPhase();
        phaser.arriveAndAwaitAdvance();
        IO.println(PHASE_D_COMPLETE.formatted(curPhase));

        // Deregister main thread
        phaser.arriveAndDeregister();

        if (phaser.isTerminated()) {
            IO.println("The Phaser is terminated");
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
            IO.println(name + " is working in phase " + phaser.getPhase());
            phaser.arriveAndAwaitAdvance(); // Wait for others
        }
    }
}
