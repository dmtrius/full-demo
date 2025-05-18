package com.example.demo.apps.tasks;

/**
 * A performance tracking utility class for measuring code execution time and providing flexible exception handling mechanisms.
 *
 * <p>Main features:
 * <ul>
 *   <li>Accurate measurement of code execution time</li>
 *   <li>Supports tracking methods with or without return values</li>
 *   <li>Provides two exception handling modes</li>
 *   <li>Supports automatic resource management</li>
 * </ul>
 *
 * <h2>Usage Examples:</h2>
 *
 * <h3>Manual tracking with try-with-resources</h3>
 * <pre>{@code
 * // Manually manage resources and track performance
 * try (TimeTracker tracker = new TimeTracker("Database Operations")) {
 *     database.connect();
 *     database.executeQuery();
 * } // Automatically closes and prints execution time
 *
 * // With return value using try-with-resources
 * try (TimeTracker tracker = new TimeTracker("Complex Calculation");
 *      Resource resource = acquireResource()) {
 *     return performComplexCalculation(resource);
 * }
 * }</pre>
 *
 * <h3>Static method with try-with-resources</h3>
 * <pre>{@code
 * try (TimeTracker ignored = TimeTracker.of("Network Request")) {
 *     httpClient.sendRequest();
 *     httpClient.receiveResponse();
 * }
 * }</pre>
 *
 * <p>Note: Using try-with-resources ensures proper resource closure and automatic recording of execution time.</p>
 *
 * <h3>Lambda with automatic exception handling</h3>
 * <pre>{@code
 * // Void method
 * TimeTracker.track("Data Processing", () -> {
 *     processData(); // Method that may throw an exception
 * });
 *
 * // Method with a return value
 * String result = TimeTracker.track("Query User", () -> {
 *     return userService.findById(123);
 * });
 * }</pre>
 *
 * <h3>Lambda with explicit exception handling</h3>
 * <pre>{@code
 * try {
 *     // Allows throwing the original exception
 *     String result = TimeTracker.trackThrows("Complex Query", () -> {
 *         return complexQuery(); // May throw a checked exception
 *     });
 * } catch (SQLException e) {
 *     // Handle specific exceptions precisely
 *     logger.error("Database query failed", e);
 * }
 * }</pre>
 *
 * <h3>Nested usage of lambdas</h3>
 * <pre>{@code
 * TimeTracker.track("Entire Workflow", () -> {
 *     // Sub-task 1
 *     TimeTracker.track("Data Preparation", () -> prepareData());
 *
 *     // Sub-task 2
 *     return TimeTracker.track("Data Processing", () -> processData());
 * });
 * }</pre>
 *
 * <p>Note: By default, the execution time is printed to the console. For production environments,
 * it is recommended to customize the logging mechanism as needed.</p>
 *
 * @author [Your Name]
 * @version 1.0
 * @since [Version Number]
 */
public class TimeTracker implements AutoCloseable {
    /** Operation name */
    private final String operationName;
    /** Start time (in nanoseconds) */
    private final long startTime;
    /** Enable logging */
    private final boolean logEnabled;

    /**
     * Creates a new TimeTracker instance.
     *
     * @param operationName Name of the operation to track
     */
    public TimeTracker(String operationName) {
        this(operationName, true);
    }

    /**
     * Private constructor for creating a TimeTracker instance.
     *
     * @param operationName Operation name
     * @param logEnabled Whether logging is enabled
     */
    private TimeTracker(String operationName, boolean logEnabled) {
        this.operationName = operationName;
        this.startTime = System.nanoTime();
        this.logEnabled = logEnabled;
        if (logEnabled) {
            System.out.printf("Execution started: %s%n", operationName);
        }
    }

    /**
     * Static factory method to create a new TimeTracker instance.
     *
     * @param operationName Name of the operation to track
     * @return A new TimeTracker instance
     */
    public static TimeTracker of(String operationName) {
        return new TimeTracker(operationName);
    }

    /**
     * Tracks the execution time of a block of code with a return value, wrapping exceptions as RuntimeException.
     *
     * @param operationName Name of the operation
     * @param execution Code block to execute
     * @param <T> Return type
     * @return Execution result
     * @throws RuntimeException If an exception occurs during execution
     */
    public static <T> T track(String operationName, ThrowableSupplier<T> execution) {
        try {
            return trackThrows(operationName, execution);
        } catch (Exception e) {
            throw new RuntimeException("Execution failed: " + operationName, e);
        }
    }

    /**
     * Tracks the execution time of a block of code with a return value, allowing exceptions to be thrown.
     *
     * @param operationName Name of the operation
     * @param execution Code block to execute
     * @param <T> Return type
     * @return Execution result
     * @throws Exception If an exception occurs during execution
     */
    public static <T> T trackThrows(String operationName, ThrowableSupplier<T> execution) throws Exception {
        try (TimeTracker ignored = new TimeTracker(operationName, true)) {
            return execution.get();
        }
    }

    /**
     * Tracks the execution time of a block of code without a return value, wrapping exceptions as RuntimeException.
     *
     * @param operationName Name of the operation
     * @param execution Code block to execute
     * @throws RuntimeException If an exception occurs during execution
     */
    public static void track(String operationName, ThrowableRunnable execution) {
        try {
            trackThrows(operationName, execution);
        } catch (Exception e) {
            throw new RuntimeException("Execution failed: " + operationName, e);
        }
    }

    /**
     * Tracks the execution time of a block of code without a return value, allowing exceptions to be thrown.
     *
     * @param operationName Name of the operation
     * @param execution Code block to execute
     * @throws Exception If an exception occurs during execution
     */
    public static void trackThrows(String operationName, ThrowableRunnable execution) throws Exception {
        try (TimeTracker ignored = new TimeTracker(operationName, true)) {
            execution.run();
        }
    }

    @Override
    public void close() {
        if (logEnabled) {
            // Calculate execution time (convert to milliseconds)
            long timeElapsed = (System.nanoTime() - startTime) / 1_000_000;
            System.out.printf("%s completed, time taken: %d ms%n", operationName, timeElapsed);
        }
    }

    /**
     * Functional interface for a Supplier that can throw exceptions.
     *
     * @param <T> Return type
     */
    @FunctionalInterface
    public interface ThrowableSupplier<T> {
        /**
         * Gets the result.
         *
         * @return Execution result
         * @throws Exception If an error occurs during execution
         */
        T get() throws Exception;
    }

    /**
     * Functional interface for a Runnable that can throw exceptions.
     */
    @FunctionalInterface
    public interface ThrowableRunnable {
        /**
         * Executes an operation.
         *
         * @throws Exception If an error occurs during execution
         */
        void run() throws Exception;
    }
}
