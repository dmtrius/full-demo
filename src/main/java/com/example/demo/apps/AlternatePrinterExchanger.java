package com.example.demo.apps;

import java.util.concurrent.Exchanger;

import static java.lang.IO.print;

public class AlternatePrinterExchanger {
    private static final Exchanger<Object> exchanger = new Exchanger<>();
    private static int number = 1;
    private static char letter = 'A';
    void main() {
        Thread numberThread = getNumbers();
        Thread charThread = getChars();

        numberThread.start();
        charThread.start();
    }

    private static Thread getNumbers() {
        return new Thread(() -> {
            while (number <= 26) { // Print numbers up to 26 (for A-Z)
                try {
                    print(number + ", ");
                    number++;
                    exchanger.exchange(null); // Exchange with the other thread
                } catch (InterruptedException _) {
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
                    exchanger.exchange(null); // Exchange with the other thread
                    print(letter);
                    if (letter != 'Z') {
                        print(", ");
                    }
                    letter++;
                } catch (InterruptedException _) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        });
    }
}
