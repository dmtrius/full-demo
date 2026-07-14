package com.example.demo.apps.tasks;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;

import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import static org.apache.commons.lang.math.NumberUtils.isNumber;

@Slf4j
public class FactorialCalculator {
    private static final int MAX_CALCULATIONS_PER_SECOND = 100;
    private static final BlockingQueue<Integer> inputQueue = new LinkedBlockingQueue<>();
    private static final BlockingQueue<Result> resultQueue = new LinkedBlockingQueue<>();
    private static final AtomicLong calculationsInCurrentSecond = new AtomicLong(0);
    private static volatile long currentSecond = System.currentTimeMillis() / 1000;

    record Result(
        int number,
        BigInteger factorial,
        int index) {
    }

    void main() {
        Scanner scanner = new Scanner(System.in);
        IO.print("Enter number of calculation threads: ");
        int numThreads = scanner.nextInt();
        scanner.close();

        Thread readerThread = new Thread(() -> readInputFile("input.txt"));
        readerThread.start();

        Thread writerThread = new Thread(() -> writeOutputFile("output.txt"));
        writerThread.start();

        try (ExecutorService executor = Executors.newFixedThreadPool(numThreads)) {
            // Submit calculation tasks
            while (true) {
                try {
                    int number = inputQueue.take();
                    IO.println("TAKING: " + number);
                    if (number == -1) {
                        break; // Null indicates the end of input
                    }
                    executor.submit(() -> calculateFactorial(number));
                } catch (InterruptedException _) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    private static void readInputFile(@NonNull String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String pNumber = line.trim();
                    if (!isNumber(pNumber)) {
                        continue;
                    }
                    int number = Integer.parseInt(pNumber);
                    if (number >= 0) {
                        inputQueue.put(number);
                    } else {
                        log.error("Skipping negative number: {}", number);
                    }
                } catch (NumberFormatException e) {
                    log.error("Invalid number format in input: '{}'", e.getMessage(), e);
                } catch (InterruptedException _) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } catch (IOException e) {
            log.error("Error reading input file: {}", e.getMessage(), e);
        } finally {
            try {
                inputQueue.put(-1); // Signal end of input
            } catch (InterruptedException _) {
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
        } catch (InterruptedException _) {
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
                } catch (InterruptedException _) {
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
                if (result.number == -1 && result.factorial.equals(BigInteger.ZERO)) {
                    break; // End signal
                }
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
            log.error("Error writing output file: {}", e.getMessage(), e);
        } catch (InterruptedException _) {
            Thread.currentThread().interrupt();
        }
    }
}
