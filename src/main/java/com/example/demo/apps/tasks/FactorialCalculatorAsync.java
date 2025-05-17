package com.example.demo.apps.tasks;

import java.io.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.IntPredicate;

public class FactorialCalculatorAsync {
    private static final int MAX_CALCULATIONS_PER_SECOND = 100;
    private static final int MAX_NUMBER_OF_THREADS = 10;
    private static final int BATCH_WRITE_SIZE = 1000;
    private static final BlockingQueue<Result> resultQueue = new LinkedBlockingQueue<>(1000);
    private static int resultsCount = 0;
    private static final int COUNTS_SHOW = 100;
    private static final Result NAN = new Result(-1, -1, BigInteger.ZERO);
    private static String basePath = "./";
    private static final String INPUT = "input.txt";
    private static final String OUTPUT = "output.txt";
    private static final String VALUE_WARNING = "That's not a valid number!";
    private static final String SEPARATOR = " = ";

    private static final Semaphore rateLimiter = new Semaphore(MAX_CALCULATIONS_PER_SECOND);
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    static {
        scheduler.scheduleAtFixedRate(() -> {
            int permitsToAdd = MAX_CALCULATIONS_PER_SECOND - rateLimiter.availablePermits();
            if (permitsToAdd > 0) {
                rateLimiter.release(permitsToAdd);
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private record InputTask(int number, int index) {
    }

    private record Result(int number, int index, BigInteger factorial) {
    }

    public static void main(String... args) {
        if (args.length == 0) {
            System.out.println("Usage: java FactorialCalculatorAsync [basePath]");
            System.out.println("basePath - path to input/output files");
            System.exit(1);
        }
        if (!Objects.isNull(args[0]) && !args[0].isEmpty()) {
            basePath = args[0];
        }
        Scanner scanner = new Scanner(System.in);
        final int numThreads = getInputValue(
                scanner,
                String.format("Enter number of calculation threads (1 - %d): ", MAX_NUMBER_OF_THREADS),
                n -> n < 1 || n > MAX_NUMBER_OF_THREADS);
        resultsCount = getInputValue(
                scanner,
                "Enter results count (0 for all): ",
                n -> n < 0);
        scanner.close();

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        System.out.println("Started...");

        Thread writerThread = new Thread(() -> writeOutputFile(basePath + OUTPUT));
        writerThread.start();

        CompletableFuture
                .supplyAsync(() -> readInputFile(basePath + INPUT), executor)
                .thenComposeAsync(inputTasks -> {
                    List<CompletableFuture<Void>> futures = new ArrayList<>();

                    for (InputTask task : inputTasks) {
                        futures.add(CompletableFuture.runAsync(() -> {
                            rateLimit();
                            calculateFactorial(task);
                        }, executor));
                    }

                    return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
                }, executor)
                .thenRun(() -> {
                    try {
                        resultQueue.put(NAN); // poison pill after all are done
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                })
                .join();

        executor.shutdown();
        shutdown();

        try {
            writerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Completed.");
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
                if (!line.matches("^\\d+$")) {
                    continue;
                }
                inputTasks.add(new InputTask(Integer.parseInt(line), index++));
                if (index % COUNTS_SHOW == 0) {
                    System.out.println("Read " + index + " lines...");
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading input file: " + e.getLocalizedMessage());
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

    public static void rateLimit() {
        try {
            rateLimiter.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    public static void shutdown() {
        scheduler.shutdownNow();
    }

    private static void writeOutputFile(String filename) {
        Map<Integer, Result> resultMap = new TreeMap<>();
        int expectedIndex = 0;
        List<String> writeBuffer = new ArrayList<>(BATCH_WRITE_SIZE);

        Path outputPath = Paths.get(filename);
        try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(
                outputPath,
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {

            long filePosition = 0;

            while (true) {
                Result result = resultQueue.take();

                if (NAN.equals(result)) {
                    flushBuffer(channel, writeBuffer, filePosition);
                    break;
                }

                resultMap.put(result.index, result);

                while (resultMap.containsKey(expectedIndex)) {
                    Result r = resultMap.remove(expectedIndex++);
                    writeBuffer.add(r.number + SEPARATOR + r.factorial);
                    if (expectedIndex % COUNTS_SHOW == 0) {
                        System.out.println("Wrote " + expectedIndex + " lines...");
                    }
                    if (writeBuffer.size() >= BATCH_WRITE_SIZE) {
                        filePosition = flushBuffer(channel, writeBuffer, filePosition);
                    }
                }
            }

        } catch (IOException | InterruptedException e) {
            System.out.println("Error writing output file: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private static long flushBuffer(AsynchronousFileChannel channel, List<String> buffer, long position) throws IOException {
        if (buffer.isEmpty()) {
            return position;
        }

        StringBuilder sb = new StringBuilder();
        for (String line : buffer) {
            sb.append(line).append(System.lineSeparator());
        }
        buffer.clear();

        byte[] data = sb.toString().getBytes(StandardCharsets.UTF_8);
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);

        Future<Integer> result = channel.write(byteBuffer, position);
        try {
            int written = result.get();
            return position + written;
        } catch (Exception e) {
            throw new IOException("Failed to write to file", e);
        }
    }

    private static int getInputValue(Scanner scanner,
                                     String message,
                                     IntPredicate predicate) {
        int result;
        do {
            System.out.print(message);
            while (!scanner.hasNextInt()) {
                System.out.println(VALUE_WARNING);
                scanner.next();
                System.out.print(message);
            }
            result = scanner.nextInt();
        } while (predicate.test(result));
        return result;
    }
}
