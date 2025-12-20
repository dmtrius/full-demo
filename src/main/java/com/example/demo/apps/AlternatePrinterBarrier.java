package com.example.demo.apps;

import java.util.concurrent.CyclicBarrier;

import static java.lang.IO.print;

public class AlternatePrinterBarrier {
    private static final CyclicBarrier barrier = new CyclicBarrier(2);
    private static int number = 1;
    private static char letter = 'A';
    private static volatile boolean isCompleted = false;

    void main() {
        Thread numberThread = getNumbers();
        Thread charThread = getChars();

        numberThread.start();
        charThread.start();
    }

    private static Thread getNumbers() {
        return new Thread(() -> {
            while (!isCompleted) {
                try {
                    barrier.await();
                    print(number);
                    if (number < 26) {
                        print(", ");
                    }
                    number++;
                    barrier.await();
                } catch (Exception _) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        });
    }

    private static Thread getChars() {
        return new Thread(() -> {
            while (letter <= 'Z') {
                try {
                    barrier.await();
                    print(letter + ", ");
                    letter++;
                    barrier.await();
                    if (letter == 'Z') {
                        isCompleted = true;
                    }
                } catch (Exception _) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        });
    }
}
