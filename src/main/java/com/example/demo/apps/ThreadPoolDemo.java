package com.example.demo.apps;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

///
///*markdown*
/// <ul>
/// <li>one</li>
/// <li>two</li>
/// </ul>
///
public class ThreadPoolDemo {
    void main() {
        ThreadPool threadPool = new ThreadPool(3);

        // Submit tasks to the thread pool
        for (int i = 0; i < 10; i++) {
            threadPool.execute(new Task("Task-" + i));
        }

        try {
            sleep(5000); // Let tasks run for 5 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        threadPool.shutdown();
    }
}

class Task implements Runnable {
    private String name;

    public Task(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        try {
            System.out.println("Executing " + name + " by " + Thread.currentThread().getName());
            TimeUnit.MILLISECONDS.sleep(1000);
            System.out.println(name + " completed by " + Thread.currentThread().getName());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class ThreadPool {
    private final BlockingQueue<Runnable> taskQueue;
    private final Thread[] workers;
    private volatile boolean isRunning = true;

    public ThreadPool(int poolSize) {
        this.taskQueue = new LinkedBlockingQueue<>();
        this.workers = new Thread[poolSize];

        for (int i = 0; i < poolSize; i++) {
            workers[i] = new Worker("Worker-" + i);
            workers[i].start();
        }
    }

    public void execute(Runnable task) {
        if (isRunning) {
            try {
                taskQueue.put(task);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } else {
            throw new IllegalStateException("ThreadPool is shutdown");
        }
    }

    public void shutdown() {
        isRunning = false;
        for (Thread worker : workers) {
            worker.interrupt();
        }
    }

    private class Worker extends Thread {
        public Worker(String name) {
            super(name);
        }

        @Override
        public void run() {
            while (isRunning || !taskQueue.isEmpty()) {
                Runnable task = taskQueue.poll();
                if (task != null) {
                    task.run();
                }
            }
        }
    }
}
