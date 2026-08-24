package com.example.demo.apps.algo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class TcpLoadBalancer {
    private final int listenPort;
    private final List<InetSocketAddress> backends;
    private final AtomicInteger rr = new AtomicInteger(0);
    private final ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor();
    private AtomicBoolean isRunning = new AtomicBoolean(true);

    public TcpLoadBalancer(int listenPort, List<InetSocketAddress> backends) {
        this.listenPort = listenPort;
        this.backends = backends;
    }

    @SuppressWarnings({"java:S2189", "java:S108"})
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(listenPort)) {
            while (isRunning.get()) {
                Socket client = serverSocket.accept();
                pool.submit(() -> handle(client));
            }
        } catch (IOException _) {}
    }

    @SuppressWarnings({"java:S2142", "java:S108"})
    private void handle(Socket client) {
        InetSocketAddress backend = pickBackend();
        try (client; Socket server = new Socket()) {

            server.connect(backend, 3000);
            server.setTcpNoDelay(true);
            client.setTcpNoDelay(true);

            Thread t1 = Thread.ofVirtual().start(() -> pipe(client, server));
            Thread t2 = Thread.ofVirtual().start(() -> pipe(server, client));
            t1.join();
            t2.join();
        } catch (IOException | InterruptedException _) {}
    }

    private InetSocketAddress pickBackend() {
        int i = Math.floorMod(rr.getAndIncrement(), backends.size());
        return backends.get(i);
    }

    @SuppressWarnings("java:S108")
    private void pipe(Socket inSock, Socket outSock) {
        try (InputStream in = inSock.getInputStream(); OutputStream out = outSock.getOutputStream()) {
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) != -1) {
                out.write(buf, 0, n);
                out.flush();
            }
            outSock.shutdownOutput();
        } catch (IOException _) {}
    }

    static void main() {
        var backends = List.of(
            new InetSocketAddress("127.0.0.1", 8001),
            new InetSocketAddress("127.0.0.1", 8002)
        );
        new TcpLoadBalancer(9000, backends).start();
    }
}
