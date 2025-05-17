package com.example.demo.apps.tasks;

import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class FactorialCalculatorAsync {
    private static final int MAX_CALCULATIONS_PER_SECOND = 100;
    private static final BlockingQueue<Result> resultQueue = new LinkedBlockingQueue<>();
    private static final AtomicLong calculationsInCurrentSecond = new AtomicLong(0);
    private static volatile long currentSecond = System.currentTimeMillis() / 1000;

    private static int resultsCount = 0;
    private static final Result NAN = new Result(-1, -1, BigInteger.ZERO);
    private static final String BASE_PATH = "C:\\work\\full-demo\\src\\main\\java\\com\\example\\demo\\apps\\tasks\\";

    private record InputTask(int number, int index) {}
    private record Result(int number, int index, BigInteger factorial) {}

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter number of calculation threads: ");
        final int numThreads = scanner.nextInt();
        System.out.print("Enter results count (0 for all): ");
        resultsCount = scanner.nextInt();
        scanner.close();

        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

        CompletableFuture<List<InputTask>> inputFuture =
                CompletableFuture.supplyAsync(() -> readInputFile(BASE_PATH + "input.txt"), executor);

        inputFuture.thenComposeAsync(inputTasks -> {
                    List<CompletableFuture<Void>> futures = new ArrayList<>();
                    Semaphore rateLimiter = new Semaphore(MAX_CALCULATIONS_PER_SECOND);

                    for (InputTask task : inputTasks) {
                        futures.add(CompletableFuture.runAsync(() -> {
                            rateLimit(rateLimiter);
                            calculateFactorial(task);
                        }, executor));
                    }

                    return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
                }, executor).thenRunAsync(() -> {
                    try {
                        resultQueue.put(NAN);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }, executor).thenRunAsync(() -> writeOutputFile(BASE_PATH + "output.txt"), executor)
                .thenRun(() -> {
                    executor.shutdown();
                    System.out.println("Completed.");
                });
    }

    private static List<InputTask> readInputFile(String filename) {
        List<InputTask> inputTasks = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int index = 0;
            while ((line = reader.readLine()) != null) {
                if (resultsCount > 0 && index >= resultsCount) {
                    break;
                }
                line = line.trim();
                if (!line.matches("^\\d+$")) continue;
                inputTasks.add(new InputTask(Integer.parseInt(line), index++));
            }
        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
        }
        return inputTasks;
    }

    private static void calculateFactorial(InputTask task) {
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

    private static void rateLimit(Semaphore rateLimiter) {
        long now = System.currentTimeMillis() / 1000;
        synchronized (FactorialCalculatorAsync.class) {
            if (now != currentSecond) {
                currentSecond = now;
                calculationsInCurrentSecond.set(0);
                rateLimiter.drainPermits();
                rateLimiter.release(MAX_CALCULATIONS_PER_SECOND);
            }
        }

        try {
            rateLimiter.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void writeOutputFile(String filename) {
        Map<Integer, Result> resultMap = new TreeMap<>();
        int expectedIndex = 0;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            while (true) {
                Result result = resultQueue.take();
                if (NAN.equals(result)) break;

                resultMap.put(result.index, result);
                while (resultMap.containsKey(expectedIndex)) {
                    Result r = resultMap.remove(expectedIndex);
                    writer.write(r.number + " = " + r.factorial);
                    writer.newLine();
                    expectedIndex++;
                }
                writer.flush();
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error writing output file: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
