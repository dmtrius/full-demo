package com.example.demo.apps.tasks;

import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class FactorialCalculatorStructured {
    private static final int MAX_CALCULATIONS_PER_SECOND = 100;
    private static final BlockingQueue<Result> resultQueue = new LinkedBlockingQueue<>();
    private static final AtomicLong calculationsInCurrentSecond = new AtomicLong(0);
    private static volatile long currentSecond = System.currentTimeMillis() / 1000;

    private static int resultsCount = 0;
    private static final Result NAN = new Result(-1, -1, BigInteger.ZERO);

    private static final String BASE_PATH = "/Users/dmytrogordiienko/Documents/GitHub/demo/src/main/java/com/example/demo/apps/tasks/";

    private record InputTask(int number, int index) {}
    private record Result(int number, int index, BigInteger factorial) {}

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter number of calculation threads: ");
        int numThreads = scanner.nextInt();
        System.out.print("Enter results count (0 for all): ");
        resultsCount = scanner.nextInt();
        scanner.close();

        List<InputTask> tasks = readInputFile(BASE_PATH + "input.txt");

        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            List<StructuredTaskScope.Subtask<Result>> futures = new ArrayList<>();

            Semaphore rateLimiter = new Semaphore(MAX_CALCULATIONS_PER_SECOND);

            for (InputTask task : tasks) {
                futures.add(scope.fork(() -> {
                    rateLimit(rateLimiter);
                    return calculateFactorial(task);
                }));
            }

            scope.join();  // Wait for all subtasks
            scope.throwIfFailed();

            for (var subtask : futures) {
                resultQueue.put(subtask.get());
            }

            resultQueue.put(NAN); // Signal writer to stop

            scope.fork(() -> {
                writeOutputFile(BASE_PATH + "output.txt");
                return null;
            });

            scope.join();  // Wait for writer to finish
        }

        System.out.println("Completed.");
    }

    private static List<InputTask> readInputFile(String filename) {
        List<InputTask> inputTasks = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int index = 0;
            while ((line = reader.readLine()) != null) {
                if (resultsCount > 0 && index >= resultsCount) break;
                line = line.trim();
                if (!line.matches("^\\d+$")) continue;
                inputTasks.add(new InputTask(Integer.parseInt(line), index++));
            }
        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
        }
        return inputTasks;
    }

    private static Result calculateFactorial(InputTask task) {
        BigInteger factorial = BigInteger.ONE;
        for (int i = 1; i <= task.number; i++) {
            factorial = factorial.multiply(BigInteger.valueOf(i));
        }
        return new Result(task.number, task.index, factorial);
    }

    private static void rateLimit(Semaphore rateLimiter) {
        long now = System.currentTimeMillis() / 1000;
        synchronized (FactorialCalculatorStructured.class) {
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
