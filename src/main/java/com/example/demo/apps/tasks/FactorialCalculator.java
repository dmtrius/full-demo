package com.example.demo.apps.tasks;

import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FactorialCalculator {
    // Shared queue for reading thread to pass numbers to calculation threads
    private static final BlockingQueue<Integer> inputQueue = new LinkedBlockingQueue<>();
    // Shared queue for calculation threads to pass results to writing thread
    private static final BlockingQueue<Result> resultQueue = new LinkedBlockingQueue<>();
    // To maintain order of results
    private static final TreeMap<Integer, Result> orderedResults = new TreeMap<>();
    // Counter for rate limiting
    private static final AtomicInteger calculationCount = new AtomicInteger(0);
    private static volatile long lastResetTime = System.currentTimeMillis();
    private static final int MAX_CALCULATIONS_PER_SECOND = 100;

    // Class to hold result with original index
    static class Result {
        int index;
        int number;
        BigInteger factorial;

        Result(int index, int number, BigInteger factorial) {
            this.index = index;
            this.number = number;
            this.factorial = factorial;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter number of calculation threads: ");
        int numThreads = scanner.nextInt();
        scanner.close();

        // Create thread pool
        ExecutorService calcPool = Executors.newFixedThreadPool(numThreads);
        // Create single reader and writer threads
        ExecutorService readerWriterPool = Executors.newFixedThreadPool(2);

        // Start reader thread
        readerWriterPool.submit(() -> {
            try (BufferedReader reader = new BufferedReader(new FileReader("input.txt"))) {
                String line;
                int index = 0;
                while ((line = reader.readLine()) != null) {
                    try {
                        int number = Integer.parseInt(line.trim());
                        inputQueue.put(number);
                        // Submit calculation task
                        final int currentIndex = index;
                        calcPool.submit(() -> calculateFactorial(currentIndex, number));
                        index++;
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid number format in input file: " + line);
                    }
                }
                // Signal end of input
                inputQueue.put(-1);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Start writer thread
        readerWriterPool.submit(() -> {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))) {
                int expectedIndex = 0;
                while (true) {
                    Result result = resultQueue.take();
                    // Check if done
                    if (result.number == -1) break;

                    synchronized (orderedResults) {
                        orderedResults.put(result.index, result);
                        // Write all consecutive results available
                        while (orderedResults.containsKey(expectedIndex)) {
                            Result r = orderedResults.remove(expectedIndex);
                            writer.write(String.format("%d = %d\n", r.number, r.factorial));
                            expectedIndex++;
                        }
                        writer.flush();
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Shutdown executors
        calcPool.shutdown();
        readerWriterPool.shutdown();
        try {
            calcPool.awaitTermination(1, TimeUnit.HOURS);
            readerWriterPool.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void calculateFactorial(int index, int number) {
        try {
            // Rate limiting
            synchronized (calculationCount) {
                long currentTime = System.currentTimeMillis();
                // Reset counter every second
                if (currentTime - lastResetTime >= 1000) {
                    calculationCount.set(0);
                    lastResetTime = currentTime;
                }
                // Wait if limit reached
                while (calculationCount.get() >= MAX_CALCULATIONS_PER_SECOND) {
                    Thread.sleep(100); // Wait and check again
                    currentTime = System.currentTimeMillis();
                    if (currentTime - lastResetTime >= 1000) {
                        calculationCount.set(0);
                        lastResetTime = currentTime;
                    }
                }
                calculationCount.incrementAndGet();
            }

            // Calculate factorial
            BigInteger factorial = BigInteger.ONE;
            if (number >= 0) {
                for (int i = 1; i <= number; i++) {
                    factorial = factorial.multiply(BigInteger.valueOf(i));
                }
            } else {
                factorial = BigInteger.ZERO; // Handle negative numbers if needed
            }

            // Pass result to writer
            resultQueue.put(new Result(index, number, factorial));

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
