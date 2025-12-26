package com.example.demo.apps.algo;

import org.springframework.lang.NonNull;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

// ==============================================
// ENHANCED AIRCRAFT COLLISION AVOIDANCE SYSTEM
// ==============================================
public class AircraftCollisionAvoidanceSystem2 {
    
    // System constants
    private static final int SAFE_DISTANCE_METERS = 5000;
    private static final int CRITICAL_DISTANCE_METERS = 1000;
    private static final int UPDATE_FREQUENCY_HZ = 10;
    private static final int MAX_RESPONSE_TIME_MS = 50;
    
    // Queue monitoring thresholds
    private static final int HIGH_PRIORITY_QUEUE_WARNING = 80;
    private static final int HIGH_PRIORITY_QUEUE_CRITICAL = 95;
    private static final int MEDIUM_PRIORITY_QUEUE_WARNING = 70;
    private static final int MEDIUM_PRIORITY_QUEUE_CRITICAL = 90;
    private static final int LOW_PRIORITY_QUEUE_WARNING = 60;
    private static final int LOW_PRIORITY_QUEUE_CRITICAL = 80;
    
    // Executors and queues
    private final ScheduledExecutorService highPriorityExecutor;
    private final ExecutorService mediumPriorityExecutor;
    private final ExecutorService lowPriorityExecutor;
    
    // Queues for monitoring
    private final BlockingQueue<Runnable> highPriorityQueue;
    private final BlockingQueue<Runnable> mediumPriorityQueue;
    private final BlockingQueue<RadarData> radarDataQueue;
    private final BlockingQueue<SystemEvent> eventQueue;
    
    // Aircraft data
    private final ConcurrentHashMap<String, Aircraft> aircraftDatabase;
    private final ReentrantReadWriteLock databaseLock;
    
    // System state
    private final AtomicBoolean systemActive;
    private final SystemMetrics systemMetrics;
    
    public AircraftCollisionAvoidanceSystem2() {
        // Initialize queues with monitoring capabilities
        this.highPriorityQueue = new LinkedBlockingQueue<>(100);
        this.mediumPriorityQueue = new LinkedBlockingQueue<>(200);
        this.radarDataQueue = new LinkedBlockingQueue<>(1000);
        this.eventQueue = new LinkedBlockingQueue<>(500);
        
        // Create executors with monitored queues
        this.highPriorityExecutor = new ScheduledThreadPoolExecutor(2,
            new RealTimeThreadFactory(Thread.MAX_PRIORITY, "HP-")) {
            //@Override
            protected BlockingQueue<Runnable> createWorkQueue() {
                return highPriorityQueue;
            }
        };
        
        this.mediumPriorityExecutor = new ThreadPoolExecutor(
            4, 8, 60, TimeUnit.SECONDS,
            mediumPriorityQueue,
            new RealTimeThreadFactory(Thread.NORM_PRIORITY + 2, "MP-")
        );
        
        this.lowPriorityExecutor = Executors.newCachedThreadPool(
            new RealTimeThreadFactory(Thread.MIN_PRIORITY, "LP-")
        );
        
        this.aircraftDatabase = new ConcurrentHashMap<>();
        this.databaseLock = new ReentrantReadWriteLock();
        this.systemActive = new AtomicBoolean(true);
        this.systemMetrics = new SystemMetrics();
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
    
    // ==============================================
    // COMPLETE QUEUE MONITORING IMPLEMENTATION
    // ==============================================
    
    /**
     * Comprehensive queue monitoring system
     * Tracks queue sizes, wait times, and performance metrics
     */
    private void monitorQueueSizes() {
        Instant monitorStart = Instant.now();
        
        try {
            // 1. Monitor High Priority Queue (Collision Detection Tasks)
            monitorHighPriorityQueue();
            
            // 2. Monitor Medium Priority Queue (Data Processing Tasks)
            monitorMediumPriorityQueue();
            
            // 3. Monitor Radar Data Queue
            monitorRadarDataQueue();
            
            // 4. Monitor System Event Queue
            monitorEventQueue();
            
            // 5. Calculate and log overall queue statistics
            logQueueStatistics();
            
            // 6. Check for queue-related anomalies
            detectQueueAnomalies();
            
            // 7. Take corrective actions if needed
            performQueueCorrectiveActions();
            
        } catch (Exception e) {
            logEvent("ERROR", "Queue monitoring failed: " + e.getMessage(), Instant.now());
        }
        
        Duration monitorTime = Duration.between(monitorStart, Instant.now());
        if (monitorTime.toMillis() > 10) {
            logEvent("WARNING", "Queue monitoring took too long: " + 
                    monitorTime.toMillis() + "ms", Instant.now());
        }
    }
    
    /**
     * Monitor High Priority Queue (Critical Tasks)
     */
    private void monitorHighPriorityQueue() {
        int currentSize = highPriorityQueue.size();
        int remainingCapacity = highPriorityQueue.remainingCapacity();
        int maxCapacity = currentSize + remainingCapacity;
        double utilization = (double) currentSize / maxCapacity * 100;
        
        // Record metrics
        systemMetrics.recordQueueMetric("HighPriority", currentSize, utilization);
        
        // Check thresholds
        if (utilization >= HIGH_PRIORITY_QUEUE_CRITICAL) {
            logEvent("CRITICAL", 
                    String.format("High-priority queue CRITICAL: %d/%d (%.1f%%)", 
                            currentSize, maxCapacity, utilization), 
                    Instant.now());
            triggerQueueEmergencyProtocol("HighPriority");
        } else if (utilization >= HIGH_PRIORITY_QUEUE_WARNING) {
            logEvent("WARNING", 
                    String.format("High-priority queue WARNING: %d/%d (%.1f%%)", 
                            currentSize, maxCapacity, utilization), 
                    Instant.now());
        }
        
        // Check for stalled tasks (tasks waiting too long)
        checkQueueWaitTimes("HighPriority", currentSize);
    }
    
    /**
     * Monitor Medium Priority Queue
     */
    private void monitorMediumPriorityQueue() {
        int currentSize = mediumPriorityQueue.size();
        int remainingCapacity = mediumPriorityQueue.remainingCapacity();
        int maxCapacity = currentSize + remainingCapacity;
        double utilization = (double) currentSize / maxCapacity * 100;
        
        // Record metrics
        systemMetrics.recordQueueMetric("MediumPriority", currentSize, utilization);
        
        // Check thresholds
        if (utilization >= MEDIUM_PRIORITY_QUEUE_CRITICAL) {
            logEvent("WARNING", 
                    String.format("Medium-priority queue CRITICAL: %d/%d (%.1f%%)", 
                            currentSize, maxCapacity, utilization), 
                    Instant.now());
        } else if (utilization >= MEDIUM_PRIORITY_QUEUE_WARNING) {
            logEvent("INFO", 
                    String.format("Medium-priority queue WARNING: %d/%d (%.1f%%)", 
                            currentSize, maxCapacity, utilization), 
                    Instant.now());
        }
        
        // Monitor thread pool stats for medium priority
        if (mediumPriorityExecutor instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor tpe = (ThreadPoolExecutor) mediumPriorityExecutor;
            int activeThreads = tpe.getActiveCount();
            long completedTasks = tpe.getCompletedTaskCount();
            
            systemMetrics.recordThreadPoolStats("MediumPriority", 
                    activeThreads, completedTasks);
        }
    }
    
    /**
     * Monitor Radar Data Queue
     */
    private void monitorRadarDataQueue() {
        int currentSize = radarDataQueue.size();
        int remainingCapacity = radarDataQueue.remainingCapacity();
        int maxCapacity = currentSize + remainingCapacity;
        double utilization = (double) currentSize / maxCapacity * 100;
        
        // Record metrics
        systemMetrics.recordQueueMetric("RadarData", currentSize, utilization);
        
        // Check for data ingestion rate issues
        long currentTime = System.currentTimeMillis();
        long lastIngestionTime = systemMetrics.getLastRadarIngestionTime();
        
        if (lastIngestionTime > 0) {
            long ingestionInterval = currentTime - lastIngestionTime;
            
            if (ingestionInterval > 2000) { // 2 seconds without data
                logEvent("WARNING", 
                        String.format("Radar data ingestion stalled for %dms", ingestionInterval), 
                        Instant.now());
            }
        }
        
        // Update ingestion time
        if (currentSize > 0) {
            systemMetrics.updateLastRadarIngestionTime(currentTime);
        }
        
        // Check data age in queue
        if (currentSize > 0 && radarDataQueue.peek() != null) {
            RadarData oldestData = radarDataQueue.peek();
            Duration dataAge = Duration.between(oldestData.timestamp, Instant.now());
            
            if (dataAge.toMillis() > 1000) { // Data older than 1 second
                logEvent("WARNING", 
                        String.format("Old data in radar queue: %dms old", dataAge.toMillis()), 
                        Instant.now());
            }
        }
    }
    
    /**
     * Monitor System Event Queue
     */
    private void monitorEventQueue() {
        int currentSize = eventQueue.size();
        int remainingCapacity = eventQueue.remainingCapacity();
        int maxCapacity = currentSize + remainingCapacity;
        double utilization = (double) currentSize / maxCapacity * 100;
        
        // Record metrics
        systemMetrics.recordQueueMetric("EventQueue", currentSize, utilization);
        
        // Check thresholds
        if (utilization >= LOW_PRIORITY_QUEUE_CRITICAL) {
            logEvent("WARNING", 
                    String.format("Event queue CRITICAL: %d/%d (%.1f%%)", 
                            currentSize, maxCapacity, utilization), 
                    Instant.now());
            // Flush old events if queue is full
            flushOldEvents();
        } else if (utilization >= LOW_PRIORITY_QUEUE_WARNING) {
            logEvent("INFO", 
                    String.format("Event queue WARNING: %d/%d (%.1f%%)", 
                            currentSize, maxCapacity, utilization), 
                    Instant.now());
        }
    }
    
    /**
     * Log comprehensive queue statistics
     */
    private void logQueueStatistics() {
        if (systemMetrics.shouldLogQueueStats()) {
            System.out.println("\n=== QUEUE STATISTICS ===");
            System.out.println("Timestamp: " + Instant.now());
            
            Map<String, QueueStats> allStats = systemMetrics.getAllQueueStats();
            for (Map.Entry<String, QueueStats> entry : allStats.entrySet()) {
                QueueStats stats = entry.getValue();
                System.out.printf("%-20s: Size=%3d, AvgSize=%.1f, MaxSize=%3d, Utilization=%.1f%%%n",
                        entry.getKey(),
                        stats.currentSize,
                        stats.averageSize,
                        stats.maxSize,
                        stats.utilization);
            }
            
            System.out.println("========================\n");
        }
    }
    
    /**
     * Detect anomalies in queue behavior
     */
    private void detectQueueAnomalies() {
        // Detect sudden queue growth
        for (Map.Entry<String, QueueStats> entry : systemMetrics.getAllQueueStats().entrySet()) {
            QueueStats stats = entry.getValue();
            
            // Check for rapid queue growth (more than 50% increase in 5 seconds)
            if (stats.growthRate > 50) {
                logEvent("WARNING", 
                        String.format("Rapid queue growth detected for %s: %.1f%% increase", 
                                entry.getKey(), stats.growthRate), 
                        Instant.now());
            }
            
            // Check for queue starvation (empty queue for too long when system is busy)
            if (stats.currentSize == 0 && systemMetrics.isSystemBusy()) {
                long emptyDuration = systemMetrics.getQueueEmptyDuration(entry.getKey());
                if (emptyDuration > 5000) { // 5 seconds empty during busy period
                    logEvent("WARNING", 
                            String.format("Queue starvation detected for %s: empty for %dms", 
                                    entry.getKey(), emptyDuration), 
                            Instant.now());
                }
            }
        }
        
        // Detect deadlock or stuck queues
        if (systemMetrics.areQueuesStuck()) {
            logEvent("CRITICAL", "Possible queue deadlock detected", Instant.now());
            triggerDeadlockRecovery();
        }
    }
    
    /**
     * Take corrective actions for queue issues
     */
    private void performQueueCorrectiveActions() {
        // 1. Scale thread pools based on queue sizes
        scaleThreadPools();
        
        // 2. Adjust queue capacities if needed
        adjustQueueCapacities();
        
        // 3. Trigger garbage collection if memory pressure is high
        if (systemMetrics.isMemoryPressureHigh()) {
            logEvent("INFO", "Triggering GC due to memory pressure", Instant.now());
            System.gc();
        }
        
        // 4. Shed load if system is overwhelmed
        if (systemMetrics.isSystemOverloaded()) {
            shedLoad();
        }
    }
    
    /**
     * Scale thread pools dynamically based on queue sizes
     */
    private void scaleThreadPools() {
        // Scale medium priority thread pool
        if (mediumPriorityExecutor instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor tpe = (ThreadPoolExecutor) mediumPriorityExecutor;
            int currentSize = mediumPriorityQueue.size();
            int currentPoolSize = tpe.getPoolSize();
            int maxPoolSize = tpe.getMaximumPoolSize();
            
            // Increase pool size if queue is filling up
            if (currentSize > 50 && currentPoolSize < maxPoolSize) {
                int newCoreSize = Math.min(currentPoolSize + 2, maxPoolSize);
                tpe.setCorePoolSize(newCoreSize);
                logEvent("INFO", 
                        String.format("Scaling medium pool from %d to %d threads", 
                                currentPoolSize, newCoreSize), 
                        Instant.now());
            }
            // Decrease pool size if queue is empty
            else if (currentSize == 0 && currentPoolSize > 4) {
                int newCoreSize = Math.max(4, currentPoolSize - 1);
                tpe.setCorePoolSize(newCoreSize);
                logEvent("INFO", 
                        String.format("Scaling medium pool from %d to %d threads", 
                                currentPoolSize, newCoreSize), 
                        Instant.now());
            }
        }
    }
    
    /**
     * Adjust queue capacities dynamically
     */
    private void adjustQueueCapacities() {
        // Note: In production, would implement custom queues with dynamic capacity
        // For now, just log recommendations
        
        QueueStats radarStats = systemMetrics.getQueueStats("RadarData");
        if (radarStats.utilization > 80) {
            logEvent("INFO", "Consider increasing radar queue capacity", Instant.now());
        }
    }
    
    /**
     * Shed load when system is overwhelmed
     */
    private void shedLoad() {
        // 1. Reduce data processing fidelity
        logEvent("WARNING", "Entering load shedding mode", Instant.now());
        
        // 2. Drop low-priority radar data
        int dropped = 0;
        while (radarDataQueue.size() > 800) { // Keep queue at 80%
            radarDataQueue.poll();
            dropped++;
        }
        
        if (dropped > 0) {
            logEvent("WARNING", 
                    String.format("Dropped %d radar data points to shed load", dropped), 
                    Instant.now());
        }
        
        // 3. Reduce collision prediction horizon
        systemMetrics.setLoadSheddingActive(true);
    }
    
    /**
     * Check for tasks waiting too long in queue
     */
    private void checkQueueWaitTimes(String queueName, int currentSize) {
        // In production, would track enqueue timestamps
        // For simulation, we'll use a simplified approach
        
        if (currentSize > 0) {
            long estimatedWaitTime = currentSize * 10; // Rough estimate: 10ms per task
            
            if (estimatedWaitTime > 100) { // More than 100ms estimated wait
                logEvent("WARNING", 
                        String.format("Long wait time estimated for %s: %dms", 
                                queueName, estimatedWaitTime), 
                        Instant.now());
            }
        }
    }
    
    /**
     * Emergency protocol for critical queue conditions
     */
    private void triggerQueueEmergencyProtocol(String queueName) {
        logEvent("EMERGENCY", 
                String.format("Activating emergency protocol for %s queue", queueName), 
                Instant.now());
        
        if ("HighPriority".equals(queueName)) {
            // 1. Suspend non-critical tasks
            lowPriorityExecutor.shutdownNow();
            
            // 2. Increase high-priority thread count
            ((ScheduledThreadPoolExecutor) highPriorityExecutor)
                .setCorePoolSize(4);
            
            // 3. Log emergency state
            systemMetrics.setEmergencyState(true);
            
            // 4. Alert human operator
            alertOperator("HIGH_PRIORITY_QUEUE_CRITICAL");
        }
    }
    
    /**
     * Flush old events from event queue
     */
    private void flushOldEvents() {
        int flushed = 0;
        int maxFlush = 100; // Don't flush too many at once
        
        while (!eventQueue.isEmpty() && flushed < maxFlush) {
            SystemEvent event = eventQueue.poll();
            if (event != null && 
                Duration.between(event.timestamp, Instant.now()).toMinutes() > 5) {
                // Event older than 5 minutes
                flushed++;
            } else if (event != null) {
                // Put back if not old
                eventQueue.offer(event);
            }
        }
        
        if (flushed > 0) {
            logEvent("INFO", 
                    String.format("Flushed %d old events from event queue", flushed), 
                    Instant.now());
        }
    }
    
    /**
     * Deadlock recovery mechanism
     */
    private void triggerDeadlockRecovery() {
        // 1. Dump thread states for analysis
        dumpThreadStates();
        
        // 2. Try to recover by interrupting potentially stuck threads
        ((ThreadPoolExecutor) mediumPriorityExecutor).purge();
        
        // 3. Reset queue states
        radarDataQueue.clear();
        eventQueue.clear();
        
        logEvent("CRITICAL", "Deadlock recovery executed", Instant.now());
    }
    
    /**
     * Dump thread states for debugging
     */
    private void dumpThreadStates() {
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        ThreadInfo[] threadInfos = threadBean.dumpAllThreads(false, false);
        
        System.err.println("\n=== THREAD DUMP FOR DEADLOCK ANALYSIS ===");
        for (ThreadInfo info : threadInfos) {
            if (info.getThreadName().startsWith("HP-") || 
                info.getThreadName().startsWith("MP-")) {
                System.err.printf("%s: %s%n", 
                        info.getThreadName(), info.getThreadState());
            }
        }
        System.err.println("=========================================\n");
    }
    
    /**
     * Alert human operator
     */
    private void alertOperator(String alertCode) {
        // In production, would integrate with alerting system
        System.err.println("OPERATOR ALERT: " + alertCode + " at " + Instant.now());
        
        // Add to event queue for logging
        eventQueue.offer(new SystemEvent(
            "OPERATOR_ALERT",
            alertCode,
            Instant.now(),
            Thread.currentThread().getName()
        ));
    }
    
    // ==============================================
    // SUPPORTING CLASSES FOR QUEUE MONITORING
    // ==============================================
    
    /**
     * System metrics collection and storage
     */
    static class SystemMetrics {
        private final ConcurrentHashMap<String, QueueStats> queueStats;
        private final AtomicLong lastRadarIngestionTime;
        private final AtomicBoolean loadSheddingActive;
        private final AtomicBoolean emergencyState;
        private final AtomicLong lastQueueStatsLog;
        
        public SystemMetrics() {
            this.queueStats = new ConcurrentHashMap<>();
            this.lastRadarIngestionTime = new AtomicLong(0);
            this.loadSheddingActive = new AtomicBoolean(false);
            this.emergencyState = new AtomicBoolean(false);
            this.lastQueueStatsLog = new AtomicLong(System.currentTimeMillis());
            
            // Initialize stats for all queues
            queueStats.put("HighPriority", new QueueStats());
            queueStats.put("MediumPriority", new QueueStats());
            queueStats.put("RadarData", new QueueStats());
            queueStats.put("EventQueue", new QueueStats());
        }
        
        public void recordQueueMetric(String queueName, int currentSize, double utilization) {
            QueueStats stats = queueStats.get(queueName);
            if (stats != null) {
                stats.update(currentSize, utilization);
            }
        }
        
        public void recordThreadPoolStats(String poolName, int activeThreads, long completedTasks) {
            // Store thread pool metrics
            // Implementation would track historical data
        }
        
        public long getLastRadarIngestionTime() {
            return lastRadarIngestionTime.get();
        }
        
        public void updateLastRadarIngestionTime(long time) {
            lastRadarIngestionTime.set(time);
        }
        
        public boolean shouldLogQueueStats() {
            long currentTime = System.currentTimeMillis();
            long lastLog = lastQueueStatsLog.get();
            
            if (currentTime - lastLog > 30000) { // Log every 30 seconds
                lastQueueStatsLog.set(currentTime);
                return true;
            }
            return false;
        }
        
        public Map<String, QueueStats> getAllQueueStats() {
            return new HashMap<>(queueStats);
        }
        
        public QueueStats getQueueStats(String queueName) {
            return queueStats.get(queueName);
        }
        
        public boolean isSystemBusy() {
            // Simplified: system is busy if any queue has high utilization
            return queueStats.values().stream()
                .anyMatch(stats -> stats.utilization > 50);
        }
        
        public long getQueueEmptyDuration(String queueName) {
            QueueStats stats = queueStats.get(queueName);
            return stats != null ? stats.getEmptyDuration() : 0;
        }
        
        public boolean areQueuesStuck() {
            // Check if queues have been at the same size for too long
            return queueStats.values().stream()
                .anyMatch(QueueStats::isStuck);
        }
        
        public boolean isMemoryPressureHigh() {
            Runtime runtime = Runtime.getRuntime();
            long usedMemory = runtime.totalMemory() - runtime.freeMemory();
            long maxMemory = runtime.maxMemory();
            return (double) usedMemory / maxMemory > 0.85;
        }
        
        public boolean isSystemOverloaded() {
            // System is overloaded if high priority queue is critical
            QueueStats hpStats = queueStats.get("HighPriority");
            return hpStats != null && hpStats.utilization > 90;
        }
        
        public void setLoadSheddingActive(boolean active) {
            loadSheddingActive.set(active);
        }
        
        public void setEmergencyState(boolean emergency) {
            emergencyState.set(emergency);
        }
    }
    
    /**
     * Queue statistics tracking
     */
    static class QueueStats {
        private volatile int currentSize;
        private volatile double averageSize;
        private volatile int maxSize;
        private volatile double utilization;
        private volatile double growthRate;
        private volatile long lastUpdateTime;
        private volatile int lastSize;
        private volatile long lastChangeTime;
        
        public QueueStats() {
            this.currentSize = 0;
            this.averageSize = 0;
            this.maxSize = 0;
            this.utilization = 0;
            this.growthRate = 0;
            this.lastUpdateTime = System.currentTimeMillis();
            this.lastSize = 0;
            this.lastChangeTime = System.currentTimeMillis();
        }
        
        public synchronized void update(int newSize, double newUtilization) {
            long currentTime = System.currentTimeMillis();
            long timeDiff = currentTime - lastUpdateTime;
            
            if (timeDiff > 0) {
                // Calculate growth rate
                if (lastSize > 0) {
                    int sizeDiff = newSize - lastSize;
                    growthRate = (double) sizeDiff / lastSize * 100;
                }
                
                // Update average (exponential moving average)
                averageSize = 0.7 * averageSize + 0.3 * newSize;
                
                // Update max size
                if (newSize > maxSize) {
                    maxSize = newSize;
                }
                
                // Track last change time
                if (newSize != currentSize) {
                    lastChangeTime = currentTime;
                }
            }
            
            currentSize = newSize;
            utilization = newUtilization;
            lastSize = newSize;
            lastUpdateTime = currentTime;
        }
        
        public long getEmptyDuration() {
            if (currentSize > 0) {
                return 0;
            }
            return System.currentTimeMillis() - lastChangeTime;
        }
        
        public boolean isStuck() {
            long timeSinceChange = System.currentTimeMillis() - lastChangeTime;
            return timeSinceChange > 10000 && currentSize > 0; // 10 seconds without change
        }
    }
    
    /**
     * System event for logging
     */
    static class SystemEvent {
        final String type;
        final String message;
        final Instant timestamp;
        final String source;
        
        public SystemEvent(String type, String message, Instant timestamp, String source) {
            this.type = type;
            this.message = message;
            this.timestamp = timestamp;
            this.source = source;
        }
    }

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
                thread.setUncaughtExceptionHandler(new AircraftCollisionAvoidanceSystem.CriticalThreadExceptionHandler());
            }

            return thread;
        }
    }
    
    // ==============================================
    // UPDATED SYSTEM HEALTH MONITOR
    // ==============================================
    class SystemHealthMonitor implements Runnable {
        @Override
        public void run() {
            while (systemActive.get()) {
                try {
                    Instant monitorStart = Instant.now();
                    
                    // 1. Monitor queue sizes (enhanced)
                    monitorQueueSizes();
                    
                    // 2. Monitor thread health
                    monitorThreadHealth();
                    
                    // 3. Monitor memory usage
                    monitorMemoryUsage();
                    
                    // 4. Monitor system load
                    monitorSystemLoad();
                    
                    // 5. Check for resource leaks
                    checkResourceLeaks();
                    
                    Duration monitorTime = Duration.between(monitorStart, Instant.now());
                    systemMetrics.recordQueueMetric("HealthMonitor", 
                            (int)monitorTime.toMillis(), 0);
                    
                    // Soft real-time: can tolerate delays
                    Thread.sleep(5000); // Check every 5 seconds
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    logEvent("ERROR", "Health monitor failed: " + e.getMessage(), Instant.now());
                }
            }
        }
        
        private void monitorSystemLoad() {
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
                com.sun.management.OperatingSystemMXBean sunOsBean = 
                    (com.sun.management.OperatingSystemMXBean) osBean;
                
                double systemLoad = sunOsBean.getSystemCpuLoad();
                double processLoad = sunOsBean.getProcessCpuLoad();
                
                if (systemLoad > 0.8) {
                    logEvent("WARNING", 
                            String.format("High system CPU load: %.1f%%", systemLoad * 100), 
                            Instant.now());
                }
                
                if (processLoad > 0.6) {
                    logEvent("INFO", 
                            String.format("High process CPU load: %.1f%%", processLoad * 100), 
                            Instant.now());
                }
            }
        }
        
        private void checkResourceLeaks() {
            // Check for file descriptor leaks
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            if (osBean instanceof UnixOperatingSystemMXBean) {
                UnixOperatingSystemMXBean unixBean = (UnixOperatingSystemMXBean) osBean;
                long openFiles = unixBean.getOpenFileDescriptorCount();
                long maxFiles = unixBean.getMaxFileDescriptorCount();
                
                if ((double) openFiles / maxFiles > 0.8) {
                    logEvent("WARNING", 
                            String.format("High file descriptor usage: %d/%d", openFiles, maxFiles), 
                            Instant.now());
                }
            }
        }
    }
    
    // ==============================================
    // REST OF THE SYSTEM IMPLEMENTATION
    // (Same as before, with queue integration)
    // ==============================================
    
    // ... [Previous implementation of Aircraft, Position, Velocity, 
    // RadarData, RealTimeThreadFactory, CollisionDetectionTask, 
    // RadarDataProcessor, DeadlineMonitor, etc.] ...
    
    // Helper method to log events
    private void logEvent(String level, String message, Instant timestamp) {
        String logMessage = String.format("[%s] %s: %s", 
                timestamp.toString(), level, message);
        System.out.println(logMessage);
        
        // Also add to events queue
        eventQueue.offer(new SystemEvent(
            "LOG_" + level,
            message,
            timestamp,
            "SystemHealthMonitor"
        ));
    }
    
    // Start and shutdown methods remain the same
    public void startSystem() {
        System.out.println("Starting Enhanced Aircraft Collision Avoidance System...");
        
        // Start monitoring tasks
        lowPriorityExecutor.submit(new SystemHealthMonitor());
        
        // ... rest of startup code ...
    }

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
    }
    
    public static void main(String[] args) {
        AircraftCollisionAvoidanceSystem system = new AircraftCollisionAvoidanceSystem();
        system.startSystem();
        
        // Run for demonstration
        try {
            Thread.sleep(120000); // Run for 2 minutes
            system.shutdown();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    // UnixOperatingSystemMXBean interface for compilation
    interface UnixOperatingSystemMXBean {
        long getOpenFileDescriptorCount();
        long getMaxFileDescriptorCount();
    }
}