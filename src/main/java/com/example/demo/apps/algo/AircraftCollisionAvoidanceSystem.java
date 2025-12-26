package com.example.demo.apps.algo;

import org.springframework.lang.NonNull;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.lang.IO.println;

// ==============================================
// 1. MAIN REAL-TIME SYSTEM CLASS
// ==============================================
public class AircraftCollisionAvoidanceSystem {

    // System constants
    private static final int SAFE_DISTANCE_METERS = 5000;
    private static final int CRITICAL_DISTANCE_METERS = 1000;
    private static final int UPDATE_FREQUENCY_HZ = 10; // 10 updates per second
    private static final int MAX_RESPONSE_TIME_MS = 50; // Hard real-time constraint
    public static final String THREAD_HP = "HP-";
    public static final String THREAD_MP = "MP-";
    public static final String THREAD_LP = "LP-";

    // Thread pools with different priorities
    private final ScheduledExecutorService highPriorityExecutor;
    private final ExecutorService mediumPriorityExecutor;
    private final ExecutorService lowPriorityExecutor;

    // Aircraft data
    private final ConcurrentHashMap<String, Aircraft> aircraftDatabase;
    private final ReentrantReadWriteLock databaseLock;

    // System state
    private final AtomicBoolean systemActive;
    private final CyclicBarrier systemBarrier;

    public AircraftCollisionAvoidanceSystem() {
        // Create executors with real-time characteristics
        this.highPriorityExecutor = Executors.newScheduledThreadPool(2,
            new RealTimeThreadFactory(Thread.MAX_PRIORITY, THREAD_HP));

        this.mediumPriorityExecutor = Executors.newFixedThreadPool(4,
            new RealTimeThreadFactory(Thread.NORM_PRIORITY + 2, THREAD_MP));

        this.lowPriorityExecutor = Executors.newCachedThreadPool(
            new RealTimeThreadFactory(Thread.MIN_PRIORITY, THREAD_LP));

        this.aircraftDatabase = new ConcurrentHashMap<>();
        this.databaseLock = new ReentrantReadWriteLock();
        this.systemActive = new AtomicBoolean(true);
        this.systemBarrier = new CyclicBarrier(3); // For synchronized updates
    }

    // ==============================================
    // 2. AIRCRAFT DATA CLASS (IMMUTABLE FOR THREAD SAFETY)
    // ==============================================
    public static final class Aircraft {
        private final String flightId;
        private final Position position;
        private final Velocity velocity;
        private final Instant timestamp;
        private final int altitude;

        public Aircraft(String flightId, Position position, Velocity velocity,
                       Instant timestamp, int altitude) {
            this.flightId = flightId;
            this.position = position;
            this.velocity = velocity;
            this.timestamp = timestamp;
            this.altitude = altitude;
        }

        // Getters and utility methods
        public double distanceTo(Aircraft other) {
            return position.distanceTo(other.position);
        }

        public Aircraft predictPosition(Duration timeAhead) {
            double seconds = timeAhead.toMillis() / 1000.0;
            double newLat = position.latitude + velocity.latitudePerSecond * seconds;
            double newLon = position.longitude + velocity.longitudePerSecond * seconds;
            int newAlt = altitude + (int)(velocity.verticalSpeed * seconds);

            return new Aircraft(
                flightId,
                new Position(newLat, newLon),
                velocity,
                timestamp.plus(timeAhead),
                newAlt
            );
        }
    }

    // ==============================================
    // 3. REAL-TIME THREAD FACTORY
    // ==============================================
    static class RealTimeThreadFactory implements ThreadFactory {
        private final int priority;
        private final String namePrefix;
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        public RealTimeThreadFactory(int priority, String namePrefix) {
            this.priority = priority;
            this.namePrefix = namePrefix;
        }

        @Override
        public Thread newThread(@NonNull Runnable r) {
            Thread thread = new Thread(r, namePrefix + threadNumber.getAndIncrement());
            thread.setPriority(priority);
            thread.setDaemon(false); // Non-daemon for critical threads

            // Set up real-time thread characteristics
            if (priority >= Thread.MAX_PRIORITY - 1) {
                thread.setUncaughtExceptionHandler(new CriticalThreadExceptionHandler());
            }

            return thread;
        }
    }

    // ==============================================
    // 4. REAL-TIME TASKS
    // ==============================================

    // A. High Priority Task: Collision Detection (Hard Real-Time)
    class CollisionDetectionTask implements Runnable {
        private final DeadlineMonitor deadlineMonitor;

        public CollisionDetectionTask() {
            this.deadlineMonitor = new DeadlineMonitor(MAX_RESPONSE_TIME_MS);
        }

        @Override
        public void run() {
            deadlineMonitor.start();

            try {
                // Hard real-time constraint: must complete within deadline
                if (!deadlineMonitor.isDeadlineFeasible()) {
                    triggerEmergencyProtocol();
                    return;
                }

                // Read-lock for concurrent access
                databaseLock.readLock().lock();
                try {
                    Instant now = Instant.now();

                    // Check for potential collisions
                    for (Aircraft aircraft1 : aircraftDatabase.values()) {
                        for (Aircraft aircraft2 : aircraftDatabase.values()) {
                            if (aircraft1.flightId.equals(aircraft2.flightId)) continue;

                            double currentDistance = aircraft1.distanceTo(aircraft2);

                            // Predict future positions
                            Aircraft predicted1 = aircraft1.predictPosition(Duration.ofSeconds(30));
                            Aircraft predicted2 = aircraft2.predictPosition(Duration.ofSeconds(30));
                            double predictedDistance = predicted1.distanceTo(predicted2);

                            // Real-time decision-making
                            if (currentDistance < CRITICAL_DISTANCE_METERS) {
                                // Immediate collision threat - highest priority
                                triggerEvasionManeuver(aircraft1, aircraft2);
                                logEvent("CRITICAL", "Immediate collision threat detected", now);
                            } else if (predictedDistance < SAFE_DISTANCE_METERS) {
                                // Potential future conflict
                                suggestCourseCorrection(aircraft1, aircraft2);
                                logEvent("WARNING", "Potential conflict predicted", now);
                            }
                        }
                    }

                    // Verify deadline was met
                    if (!deadlineMonitor.checkDeadline()) {
                        logEvent("ERROR", "Collision detection missed deadline", now);
                    }

                } finally {
                    databaseLock.readLock().unlock();
                }

            } catch (Exception e) {
                logEvent("FATAL", "Collision detection failed: " + e.getMessage(), Instant.now());
            } finally {
                deadlineMonitor.end();
            }
        }
    }

    // B. Medium Priority Task: Data Processing (Firm Real-Time)
    class RadarDataProcessor implements Runnable {
        private final BlockingQueue<RadarData> dataQueue;

        public RadarDataProcessor(BlockingQueue<RadarData> dataQueue) {
            this.dataQueue = dataQueue;
        }

        @Override
        public void run() {
            while (systemActive.get()) {
                try {
                    // Wait for data with timeout (firm real-time)
                    RadarData data = dataQueue.poll(100, TimeUnit.MILLISECONDS);

                    if (data != null) {
                        Instant processingStart = Instant.now();

                        // Process and update aircraft data
                        Aircraft aircraft = processRadarData(data);

                        // Write-lock for update
                        databaseLock.writeLock().lock();
                        try {
                            aircraftDatabase.put(aircraft.flightId, aircraft);
                        } finally {
                            databaseLock.writeLock().unlock();
                        }

                        Duration processingTime = Duration.between(processingStart, Instant.now());
                        if (processingTime.toMillis() > 20) {
                            logEvent("WARNING", "Radar processing taking too long", Instant.now());
                        }
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    // C. Low Priority Task: System Monitoring (Soft Real-Time)
    @SuppressWarnings("busy-waiting")
    class SystemHealthMonitor implements Runnable {
        @Override
        public void run() {
            while (systemActive.get()) {
                try {
                    // Monitor system metrics
                    monitorThreadHealth();
                    monitorMemoryUsage();
                    monitorQueueSizes();

                    // Soft real-time: can tolerate delays
                    Thread.sleep(5000); // Check every 5 seconds

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    // ==============================================
    // 5. DEADLINE MONITORING (CRITICAL FOR REAL-TIME)
    // ==============================================
    static class DeadlineMonitor {
        private final long maxDurationMs;
        private Instant startTime;
        private boolean deadlineMet;

        public DeadlineMonitor(long maxDurationMs) {
            this.maxDurationMs = maxDurationMs;
            this.deadlineMet = true;
        }

        public void start() {
            this.startTime = Instant.now();
            this.deadlineMet = true;
        }

        public boolean checkDeadline() {
            if (startTime == null) return false;

            long elapsed = Duration.between(startTime, Instant.now()).toMillis();
            deadlineMet = elapsed <= maxDurationMs;
            return deadlineMet;
        }

        public boolean isDeadlineFeasible() {
            // Check if we have enough time to start this task
            return true; // Simplified - would check system load in real implementation
        }

        public void end() {
            checkDeadline();
            if (!deadlineMet) {
                // Deadline missed - take appropriate action
                System.err.println("DEADLINE MISSED! Required: " + maxDurationMs + "ms");
            }
        }
    }

        // ==============================================
        // 6. SUPPORTING CLASSES
        // ==============================================
        private record Position(double latitude, double longitude) {

        double distanceTo(Position other) {
                // Simplified calculation - real implementation would use Haversine
                return Math.sqrt(
                    Math.pow(latitude - other.latitude, 2) +
                        Math.pow(longitude - other.longitude, 2)
                ) * 111000; // Convert to meters
            }
        }

    /**
     * @param verticalSpeed meters per second
     */
    private record Velocity(double latitudePerSecond, double longitudePerSecond, double verticalSpeed) {
    }

    private record RadarData(String flightId, double latitude, double longitude, int altitude, Instant timestamp) {
    }

    static class CriticalThreadExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            System.err.println("CRITICAL THREAD FAILURE: " + t.getName());
            e.printStackTrace();
            // In real system, would trigger emergency protocols
        }
    }

    // ==============================================
    // 7. SYSTEM CONTROL METHODS
    // ==============================================
    public void startSystem() {
        System.out.println("Starting Aircraft Collision Avoidance System...");

        // Start high-priority collision detection (10Hz)
        ScheduledFuture<?> collisionTask = highPriorityExecutor.scheduleAtFixedRate(
            new CollisionDetectionTask(),
            0, 1000 / UPDATE_FREQUENCY_HZ, TimeUnit.MILLISECONDS
        );

        // Start medium-priority radar processing
        BlockingQueue<RadarData> radarQueue = new LinkedBlockingQueue<>(1000);
        mediumPriorityExecutor.submit(new RadarDataProcessor(radarQueue));

        // Start low-priority monitoring
        lowPriorityExecutor.submit(new SystemHealthMonitor());

        // Simulate radar data input
        simulateRadarData(radarQueue);

        System.out.println("System started successfully");
    }

    public void shutdown() {
        System.out.println("Shutting down system...");
        systemActive.set(false);

        // Ordered shutdown
        highPriorityExecutor.shutdownNow();
        mediumPriorityExecutor.shutdown();
        lowPriorityExecutor.shutdown();

        try {
            if (!highPriorityExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                System.err.println("High-priority tasks did not terminate gracefully");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ==============================================
    // 8. UTILITY METHODS
    // ==============================================
    private void triggerEvasionManeuver(Aircraft aircraft1, Aircraft aircraft2) {
        // In real system: send commands to aircraft
        System.out.println("EVASION: " + aircraft1.flightId + " and " +
                          aircraft2.flightId + " - climb/descend commands issued");
    }

    private void suggestCourseCorrection(Aircraft aircraft1, Aircraft aircraft2) {
        System.out.println("ADVISORY: Course correction suggested for " +
                          aircraft1.flightId + " :: by " + aircraft2.flightId);
    }

    private void triggerEmergencyProtocol() {
        System.err.println("EMERGENCY: System cannot meet deadlines!");
        // Activate backup systems, alert operators, etc.
    }

    private Aircraft processRadarData(RadarData data) {
        // Simulate data processing
        return new Aircraft(
            data.flightId,
            new Position(data.latitude, data.longitude),
            new Velocity(0.0001, 0.0001, 10), // Dummy velocity
            data.timestamp,
            data.altitude
        );
    }

    private void logEvent(String level, String message, Instant timestamp) {
        System.out.printf("[%s] %s: %s%n",
            timestamp.toString(), level, message);
    }

    @SuppressWarnings("busy-waiting")
    private void simulateRadarData(BlockingQueue<RadarData> queue) {
        lowPriorityExecutor.submit(() -> {
            int aircraftCount = 0;
            while (systemActive.get()) {
                try {
                    // Simulate radar updates for multiple aircraft
                    for (int i = 0; i < 10; i++) {
                        RadarData data = new RadarData(
                            "FLIGHT-" + (1000 + aircraftCount % 10),
                            40.0 + Math.random() * 10,
                            -70.0 + Math.random() * 10,
                            30000 + (int)(Math.random() * 5000),
                            Instant.now()
                        );
                        if (!queue.offer(data, 10, TimeUnit.MILLISECONDS)) {
                            println("Cannot offer data: " + data);
                        }
                    }
                    aircraftCount++;
                    Thread.sleep(100); // Simulate radar update rate
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
    }

    private void monitorThreadHealth() {
        // Monitor thread states and counts
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        int threadCount = threadBean.getThreadCount();
        if (threadCount > 100) {
            logEvent("WARNING", "High thread count: " + threadCount, Instant.now());
        }
    }

    private void monitorMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long maxMemory = runtime.maxMemory();
        double usagePercentage = (double) usedMemory / maxMemory * 100;

        if (usagePercentage > 80) {
            logEvent("WARNING", "High memory usage: " + usagePercentage + "%", Instant.now());
        }
    }

    private void monitorQueueSizes() {
        // Monitor internal queue sizes
        // Implementation depends on specific queues used

    }

    // ==============================================
    // 9. MAIN METHOD
    // ==============================================
    void main() {
        AircraftCollisionAvoidanceSystem acas = new AircraftCollisionAvoidanceSystem();

        // Add shutdown hook for graceful termination
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutdown signal received");
            acas.shutdown();
        }));

        try {
            acas.startSystem();

            // Run for 60 seconds in demo
            Thread.sleep(60000);

            System.out.println("\nDemo completed - shutting down");
            acas.shutdown();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}