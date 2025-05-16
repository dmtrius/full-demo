package com.example.demo.apps.tasks;

import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class FactorialCalculatorVirtualThreads {
    private static final int MAX_CALCULATIONS_PER_SECOND = 100;
    private static final BlockingQueue<Integer> inputQueue = new LinkedBlockingQueue<>();
    private static final BlockingQueue<Result> resultQueue = new LinkedBlockingQueue<>();
    private static final AtomicLong calculationsInCurrentSecond = new AtomicLong(0);
    private static volatile long currentSecond = System.currentTimeMillis() / 1000;

    record Result(
        int number,
        BigInteger factorial,
        int index) {}

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter number of calculation threads: ");
        int numThreads = scanner.nextInt();
        scanner.close();

        // Start reader thread
        Thread readerThread = Thread.ofVirtual().start(() -> readInputFile("input.txt"));

        // Start writer thread
        Thread writerThread = Thread.ofVirtual().start(() -> writeOutputFile("output.txt"));

        // Use try-with-resources for ExecutorService with virtual threads
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            // Submit calculation tasks
            for (int i = 0; i < numThreads; i++) {
                executor.submit(() -> {
                    while (true) {
                        try {
                            Integer number = inputQueue.take();
                            if (number == -1) {
                                break; // Null indicates end of input
                            }
                            calculateFactorial(number);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                });
            }
            // Wait for reader thread to finish before closing executor
            try {
                readerThread.join();
//                writerThread.join();
                executor.shutdown();
                executor.awaitTermination(60, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static void readInputFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int index = 0;
            while ((line = reader.readLine()) != null) {
                try {
                    int number = Integer.parseInt(line.trim());
                    if (number >= 0) {
                        inputQueue.put(number);
                    } else {
                        System.err.println("Skipping negative number: " + number);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number format in input: " + line);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
        } finally {
            try {
                inputQueue.put(null); // Signal end of input
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static void calculateFactorial(int number) {
        rateLimit();
        BigInteger factorial = BigInteger.ONE;
        for (int i = 1; i <= number; i++) {
            factorial = factorial.multiply(BigInteger.valueOf(i));
        }
        try {
            resultQueue.put(new Result(number, factorial, number));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void rateLimit() {
        synchronized (FactorialCalculator.class) {
            long nowSecond = System.currentTimeMillis() / 1000;
            if (nowSecond != currentSecond) {
                currentSecond = nowSecond;
                calculationsInCurrentSecond.set(0);
            }
            while (calculationsInCurrentSecond.get() >= MAX_CALCULATIONS_PER_SECOND) {
                try {
                    Thread.sleep(1000 - (System.currentTimeMillis() % 1000));
                    nowSecond = System.currentTimeMillis() / 1000;
                    if (nowSecond != currentSecond) {
                        currentSecond = nowSecond;
                        calculationsInCurrentSecond.set(0);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            calculationsInCurrentSecond.incrementAndGet();
        }
    }

    private static void writeOutputFile(String filename) {
        TreeMap<Integer, Result> resultMap = new TreeMap<>();
        int expectedIndex = 0;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            while (true) {
                Result result = resultQueue.take();
                if (result.number == -1 && result.factorial.equals(BigInteger.ZERO)) break; // End signal
                resultMap.put(result.index, result);

                // Write all results in order up to the current expected index
                while (resultMap.containsKey(expectedIndex)) {
                    Result r = resultMap.remove(expectedIndex);
                    writer.write(r.number + " = " + r.factorial);
                    writer.newLine();
                    expectedIndex++;
                }
                writer.flush();
            }
        } catch (IOException e) {
            System.err.println("Error writing output file: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
