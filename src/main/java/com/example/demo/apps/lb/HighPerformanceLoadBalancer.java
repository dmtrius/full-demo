package com.example.demo.apps.lb;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.Collectors;

// Main Load Balancer class
public class HighPerformanceLoadBalancer {
    private final int port;
    private final List<BackendServer> servers;
    private final LoadBalancingStrategy strategy;
    private final HealthChecker healthChecker;
    private final MetricsCollector metrics;
    private final Selector selector;
    private final ExecutorService executorService;
    private volatile boolean running = false;

    public HighPerformanceLoadBalancer(int port, List<BackendServer> servers,
                                       LoadBalancingStrategy strategy) throws IOException {
        this.port = port;
        this.servers = new CopyOnWriteArrayList<>(servers);
        this.strategy = strategy;
        this.healthChecker = new HealthChecker(servers);
        this.metrics = new MetricsCollector();
        this.selector = Selector.open();
        this.executorService = Executors.newCachedThreadPool();
    }

    public void start() throws IOException {
        running = true;

        try (ServerSocketChannel serverChannel = ServerSocketChannel.open()) {
            serverChannel.configureBlocking(false);
            serverChannel.bind(new InetSocketAddress(port));
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        }

        healthChecker.start();

        System.out.println("Load Balancer started on port " + port);

        while (running) {
            selector.select();
            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                keyIterator.remove();

                if (key.isAcceptable()) {
                    acceptConnection(key);
                } else if (key.isReadable()) {
                    handleRequest(key);
                }
            }
        }
    }

    private void acceptConnection(SelectionKey key) throws IOException {
        SocketChannel clientChannel;
        try (ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel()) {
            clientChannel = serverChannel.accept();
        }

        if (clientChannel != null) {
            clientChannel.configureBlocking(false);
            clientChannel.register(selector, SelectionKey.OP_READ);
            metrics.incrementConnectionCount();
            System.out.println("New connection accepted: " + clientChannel.getRemoteAddress());
        }
    }

    private void handleRequest(SelectionKey key) {
        executorService.submit(() -> {
            try {
                SocketChannel clientChannel = (SocketChannel) key.channel();
                BackendServer server = strategy.selectServer(getHealthyServers());

                if (server == null) {
                    sendErrorResponse(clientChannel, "No healthy servers available");
                    return;
                }

                forwardRequest(clientChannel, server);

            } catch (Exception e) {
                System.err.println("Error handling request: " + e.getMessage());
                metrics.incrementErrorCount();
            }
        });
    }

    private void forwardRequest(SocketChannel clientChannel, BackendServer server) throws IOException {
        try (Socket backendSocket = new Socket(server.getHost(), server.getPort())) {
            backendSocket.setSoTimeout(5000); // 5 second timeout

            // Forward request to backend
            byte[] requestData = readFromChannel(clientChannel);
            backendSocket.getOutputStream().write(requestData);

            // Forward response back to a client
            byte[] responseData = readFromSocket(backendSocket);
            writeToChannel(clientChannel, responseData);

            server.recordSuccess();
            metrics.incrementRequestCount();

        } catch (IOException e) {
            server.recordFailure();
            sendErrorResponse(clientChannel, "Backend server error");
            throw e;
        }
    }

    private byte[] readFromChannel(SocketChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(8192);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        while (channel.read(buffer) > 0) {
            buffer.flip();
            while (buffer.hasRemaining()) {
                baos.write(buffer.get());
            }
            buffer.clear();
        }

        return baos.toByteArray();
    }

    private byte[] readFromSocket(Socket socket) throws IOException {
        InputStream input = socket.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int bytesRead;

        while ((bytesRead = input.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }

        return baos.toByteArray();
    }

    private void writeToChannel(SocketChannel channel, byte[] data) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        channel.write(buffer);
    }

    private void sendErrorResponse(SocketChannel channel, String message) {
        try {
            String response = "HTTP/1.1 503 Service Unavailable\r\n" +
                    "Content-Type: text/plain\r\n" +
                    "Content-Length: " + message.length() + "\r\n\r\n" +
                    message;
            writeToChannel(channel, response.getBytes());
            channel.close();
        } catch (IOException e) {
            System.err.println("Error sending error response: " + e.getMessage());
        }
    }

    private List<BackendServer> getHealthyServers() {
        return servers.stream()
                .filter(BackendServer::isHealthy)
                .collect(Collectors.toList());
    }

    public void stop() {
        running = false;
        healthChecker.stop();
        executorService.shutdown();
        try {
            selector.close();
        } catch (IOException e) {
            System.err.println("Error closing selector: " + e.getMessage());
        }
    }

    protected MetricsCollector getMetrics() {
        return metrics;
    }
}

// Backend Server representation
class BackendServer {
    @Getter
    private final String host;
    @Getter
    private final int port;
    private final AtomicLong requestCount = new AtomicLong(0);
    private final AtomicLong failureCount = new AtomicLong(0);
    @Setter
    @Getter
    private volatile boolean healthy = true;
    @Getter
    private final int weight;

    public BackendServer(String host, int port, int weight) {
        this.host = host;
        this.port = port;
        this.weight = weight;
    }

    public BackendServer(String host, int port) {
        this(host, port, 1);
    }

    public void recordSuccess() { requestCount.incrementAndGet(); }
    public void recordFailure() { failureCount.incrementAndGet(); }

    public long getRequestCount() { return requestCount.get(); }
    @SuppressWarnings("unused")
    public long getFailureCount() { return failureCount.get(); }

    @Override
    public String toString() {
        return host + ":" + port + " (weight=" + weight + ", healthy=" + healthy + ")";
    }
}

// Load Balancing Strategy interface
interface LoadBalancingStrategy {
    BackendServer selectServer(List<BackendServer> servers);
}

// Round Robin Strategy
class RoundRobinStrategy implements LoadBalancingStrategy {
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public BackendServer selectServer(List<BackendServer> servers) {
        if (servers.isEmpty()) return null;
        int index = counter.getAndIncrement() % servers.size();
        return servers.get(index);
    }
}

// Weighted Round Robin Strategy
class WeightedRoundRobinStrategy implements LoadBalancingStrategy {
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public BackendServer selectServer(List<BackendServer> servers) {
        if (servers.isEmpty()) return null;

        int totalWeight = servers.stream().mapToInt(BackendServer::getWeight).sum();
        int target = counter.getAndIncrement() % totalWeight;

        int currentWeight = 0;
        for (BackendServer server : servers) {
            currentWeight += server.getWeight();
            if (target < currentWeight) {
                return server;
            }
        }

        return servers.getFirst(); // Fallback
    }
}

// Least Connections Strategy
@SuppressWarnings("unused")
class LeastConnectionsStrategy implements LoadBalancingStrategy {
    @Override
    public BackendServer selectServer(List<BackendServer> servers) {
        if (servers.isEmpty()) return null;

        return servers.stream()
                .min(Comparator.comparingLong(BackendServer::getRequestCount))
                .orElse(servers.getFirst());
    }
}

// Random Strategy
@SuppressWarnings("unused")
class RandomStrategy implements LoadBalancingStrategy {
    private final Random random = new Random();

    @Override
    public BackendServer selectServer(List<BackendServer> servers) {
        if (servers.isEmpty()) return null;
        return servers.get(random.nextInt(servers.size()));
    }
}

// Health Checker
class HealthChecker {
    private final List<BackendServer> servers;
    private final ScheduledExecutorService scheduler;
    @SuppressWarnings("unused")
    private volatile boolean running = false;

    public HealthChecker(List<BackendServer> servers) {
        this.servers = servers;
        this.scheduler = Executors.newScheduledThreadPool(2);
    }

    public void start() {
        running = true;
        scheduler.scheduleAtFixedRate(this::checkHealth, 0, 10, TimeUnit.SECONDS);
    }

    private void checkHealth() {
        for (BackendServer server : servers) {
            CompletableFuture.supplyAsync(() -> {
                try (Socket socket = new Socket()) {
                    socket.connect(new InetSocketAddress(server.getHost(), server.getPort()), 3000);
                    return true;
                } catch (IOException e) {
                    return false;
                }
            }).thenAccept(healthy -> {
                boolean wasHealthy = server.isHealthy();
                server.setHealthy(healthy);

                if (wasHealthy != healthy) {
                    System.out.println("Server " + server + " health changed to: " + healthy);
                }
            });
        }
    }

    public void stop() {
        running = false;
        scheduler.shutdown();
    }
}

// Metrics Collector
class MetricsCollector {
    private final AtomicLong requestCount = new AtomicLong(0);
    private final AtomicLong errorCount = new AtomicLong(0);
    private final AtomicLong connectionCount = new AtomicLong(0);
    private final long startTime = System.currentTimeMillis();

    public void incrementRequestCount() { requestCount.incrementAndGet(); }
    public void incrementErrorCount() { errorCount.incrementAndGet(); }
    public void incrementConnectionCount() { connectionCount.incrementAndGet(); }

    public long getRequestCount() { return requestCount.get(); }
    public long getErrorCount() { return errorCount.get(); }
    public long getConnectionCount() { return connectionCount.get(); }

    public double getRequestsPerSecond() {
        long uptime = System.currentTimeMillis() - startTime;
        return uptime > 0 ? (requestCount.get() * 1000.0) / uptime : 0;
    }

    public void printStats() {
        System.out.println("=== Load Balancer Metrics ===");
        System.out.println("Total Requests: " + getRequestCount());
        System.out.println("Total Errors: " + getErrorCount());
        System.out.println("Total Connections: " + getConnectionCount());
        System.out.println("Requests/sec: " + String.format("%.2f", getRequestsPerSecond()));
        System.out.println("Error Rate: " + String.format("%.2f%%",
                getRequestCount() > 0 ? (getErrorCount() * 100.0 / getRequestCount()) : 0));
    }
}

// Example usage and main class
@Slf4j
class LoadBalancerExample {
    @SuppressWarnings("unused")
    public static void main(String[] args) {
        try {
            // Define backend servers
            List<BackendServer> servers = Arrays.asList(
                    new BackendServer("localhost", 8081, 2),
                    new BackendServer("localhost", 8082, 1),
                    new BackendServer("localhost", 8083, 3)
            );

            // Create load balancer with weighted round-robin strategy
            LoadBalancingStrategy strategy = new RandomStrategy();
            HighPerformanceLoadBalancer lb = new HighPerformanceLoadBalancer(8080, servers, strategy);

            // Add shutdown hook for graceful shutdown
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutting down load balancer...");
                lb.stop();
                lb.getMetrics().printStats();
            }));

            // Start metrics reporting
            try (ScheduledExecutorService metricsReporter = Executors.newScheduledThreadPool(1)) {
                metricsReporter.scheduleAtFixedRate(() -> lb.getMetrics().printStats(),
                        30, 30, TimeUnit.SECONDS);
            }

            // Start the load balancer
            lb.start();

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}

// Configuration class for easy setup
@SuppressWarnings("unused")
class LoadBalancerConfig {
    private int port = 8080;
    private final List<BackendServer> servers = new ArrayList<>();
    private LoadBalancingStrategy strategy = new RoundRobinStrategy();

    public LoadBalancerConfig port(int port) {
        this.port = port;
        return this;
    }

    public LoadBalancerConfig addServer(String host, int port) {
        servers.add(new BackendServer(host, port));
        return this;
    }

    public LoadBalancerConfig addServer(String host, int port, int weight) {
        servers.add(new BackendServer(host, port, weight));
        return this;
    }

    public LoadBalancerConfig strategy(LoadBalancingStrategy strategy) {
        this.strategy = strategy;
        return this;
    }

    public HighPerformanceLoadBalancer build() throws IOException {
        return new HighPerformanceLoadBalancer(port, servers, strategy);
    }
}
