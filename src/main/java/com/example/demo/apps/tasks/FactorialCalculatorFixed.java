package com.example.demo.apps.tasks;

import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FactorialCalculatorFixed {
    private static final int MAX_CALCULATIONS_PER_SECOND = 100;
    private static final BlockingQueue<InputTask> inputQueue = new LinkedBlockingQueue<>();
    private static final BlockingQueue<Result> resultQueue = new LinkedBlockingQueue<>();
    private static final AtomicLong calculationsInCurrentSecond = new AtomicLong(0);
    private static volatile long currentSecond = System.currentTimeMillis() / 1000;
    private static final Lock lock = new ReentrantLock();

    private static int resultsCount = 0;
    private static final InputTask POISON_PILL = new InputTask(-1, -1);
    private static final Result NAN = new Result(-1, -1, BigInteger.ZERO);

    private static final String BASE_PATH = "/Users/dmytrogordiienko/Documents/GitHub/demo/src/main/java/com/example/demo/apps/tasks/";

    private record InputTask(
            int number,
            int index) {
    }

    record Result(
            int number,
            int index,
            BigInteger factorial) {
    }

    @SuppressWarnings("unused")
    public static void main(String... args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter number of calculation threads: ");
        final int numThreads = scanner.nextInt();
        System.out.print("Enter results count (0 for all): ");
        resultsCount = scanner.nextInt();
        scanner.close();

        Thread readerThread = Thread.ofVirtual().start(() -> readInputFile(BASE_PATH + "input.txt", numThreads));

        Thread writerThread = Thread.ofVirtual().start(() -> writeOutputFile(BASE_PATH + "output.txt"));

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < numThreads; i++) {
                executor.submit(() -> {
                    try {
                        while (true) {
                            InputTask task = inputQueue.take();
                            if (POISON_PILL.equals(task)) {
                                break;
                            }
                            calculateFactorial(task);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
            try {
                readerThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("Calculation threads did not terminate in time.");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            try {
                resultQueue.put(NAN);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            try {
                writerThread.join();
                System.out.println("Completed.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static boolean isPositiveInteger(String s) {
        return !Objects.isNull(s) && s.matches("^\\d+$");
    }

    private static void readInputFile(String filename, int numThreads) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int index = 0;
            while ((line = reader.readLine()) != null) {
                if (resultsCount >= 0 && index >= resultsCount) {
                    break;
                }
                line = line.trim();
                if (!isPositiveInteger(line)) {
                    continue;
                }
                try {
                    int number = Integer.parseInt(line.trim());
                    inputQueue.put(new InputTask(number, index++));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
        } finally {
            for (int i = 0; i < numThreads; i++) {
                try {
                    inputQueue.put(POISON_PILL);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private static void calculateFactorial(InputTask task) {
        rateLimit();
        BigInteger factorial = BigInteger.ONE;
        for (int i = 1; i <= task.number; i++) {
            factorial = factorial.multiply(BigInteger.valueOf(i));
        }
        try {
            resultQueue.put(new Result(task.number, task.index, factorial));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void rateLimit() {
        if (lock.tryLock()) {
            try {
                long nowSecond = System.currentTimeMillis() / 1000;
                if (nowSecond != currentSecond) {
                    currentSecond = nowSecond;
                    calculationsInCurrentSecond.set(0);
                }
                while (calculationsInCurrentSecond.get() >= MAX_CALCULATIONS_PER_SECOND) {
                    long sleepMillis = 1000 - (System.currentTimeMillis() % 1000);
                    try {
                        TimeUnit.MILLISECONDS.sleep(sleepMillis);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                    nowSecond = System.currentTimeMillis() / 1000;
                    if (nowSecond != currentSecond) {
                        currentSecond = nowSecond;
                        calculationsInCurrentSecond.set(0);
                    }
                }
                calculationsInCurrentSecond.incrementAndGet();
            } finally {
                lock.unlock();
            }
        }
    }

    private static void writeOutputFile(String filename) {
        Map<Integer, Result> resultMap = new TreeMap<>();
        int expectedIndex = 0;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            while (true) {
                Result result = resultQueue.take();
                if (NAN.equals(result)) {
                    break;
                }
                resultMap.put(result.index, result);
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
