package com.example.demo.apps.tasks;

import java.util.Arrays;
import java.util.List;

public class CoroutineMain {
    public static void main(String[] args) {
        Coroutine bob = new Coroutine() {
            public void corun() throws InterruptedException {
                count("Bob", this);
            }
        };
        Coroutine alice = new Coroutine() {
            public void corun() throws InterruptedException {
                count("Alice", this);
            }
        };
        Coroutine tom = new Coroutine() {
            public void corun() throws InterruptedException {
                count("Tom", this);
            }
        };
        List<Coroutine> coroutines = Arrays.asList(bob, alice, tom);
//        Coroutines.executeAll(coroutines);
    }

    private static void count(String name, Coroutine co) throws InterruptedException {
        for (int i = 1; i <= 100; i++) {
            System.out.println(name + ": " + i);
            if (i % 3 == 0) {
                co.yield(); // yield control every 3 counts
            }
        }
        co.end();
    }
}

interface Coroutine {
    void corun() throws InterruptedException;
    default void yield() throws InterruptedException {
//        Context.getInstance(this).yield(this);
    }
    default void end() throws InterruptedException {
//        Context.getInstance(this).end(this);
    }
}
