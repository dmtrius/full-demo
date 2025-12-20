package com.example.demo.apps;

import static java.lang.IO.print;

public class AlternatePrinter {
    private static final Object lock = new Object();
    private static volatile boolean isNumberTurn = true;
    private static volatile boolean isCompleted = false;

    void main() {
        Thread numberThread = getNumbers();
        Thread charThread = getChars();

        numberThread.start();
        charThread.start();
    }

    private static Thread getNumbers() {
        return new Thread(() -> {
            int i = 1;
            while (!isCompleted) {
                synchronized (lock) {
                    while (!isNumberTurn) {
                        try {
                            lock.wait();
                        } catch (InterruptedException _) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    print(i + ", ");
                    i++;
                    isNumberTurn = false;
                    lock.notifyAll();
                }
            }
        });
    }

    private static Thread getChars() {
        return new Thread(() -> {
            char c = 'A';
            while (c <= 'Z') {
                synchronized (lock) {
                    while (isNumberTurn) {
                        try {
                            lock.wait();
                        } catch (InterruptedException _) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    print(c);
                    if (c != 'Z') {
                        print(", ");
                    }
                    c++;
                    isNumberTurn = true;
                    lock.notifyAll();
                }
                if (c == 'Z') {
                    isCompleted = true;
                }
            }
        });
    }
}
