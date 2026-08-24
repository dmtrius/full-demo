package com.example.demo.apps.lb;

import com.example.demo.apps.sockets.NIO2AsyncServer;
import com.example.demo.apps.sockets.NIOSocketServer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class TcpLoadBalancer {
    private final int listenPort;
    private final List<InetSocketAddress> backends;
    private final AtomicInteger rr = new AtomicInteger(0);
    private final ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor();
    private final AtomicBoolean isRunning = new AtomicBoolean(true);

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
        } catch (IOException e) {
            log.error("Error occurred while starting the load balancer", e);
        }
    }

    @SuppressWarnings({"java:S2142", "java:S108"})
    private void handle(Socket client) {
        InetSocketAddress backend = pickBackend();
        try (client; Socket server = new Socket()) {

            server.connect(backend, 3000);
            server.setTcpNoDelay(true);
            client.setTcpNoDelay(true);

            Thread t1 = new Thread(() -> pipe(client, server));
            Thread t2 = new Thread(() -> pipe(server, client));
            t1.start();
            t2.start();
            t1.join();
            t2.join();
        } catch (IOException | InterruptedException e) {
            log.error("Error occurred while handling client connection", e);
        }
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

            if (in.markSupported()) {
                in.mark(8192);
            }
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(in, StandardCharsets.UTF_8)
            );
            String line = br.readLine();
            if ("_quit()".equals(line)) {
                isRunning.set(false);
                return;
            } else if (in.markSupported()) {
                in.reset();
            }

            while ((n = in.read(buf)) != -1) {
                out.write(buf, 0, n);
                out.flush();
            }
            outSock.shutdownOutput();
        } catch (IOException e) {
            log.error("Error occurred while piping data", e);
        }
    }

    private static final int FE_PORT = 9000;
    private static final int BE_PORT1 = 8001;
    private static final int BE_PORT2 = 8002;

    static void main() {
        var backends = List.of(
                new InetSocketAddress("127.0.0.1", BE_PORT1),
                new InetSocketAddress("127.0.0.1", BE_PORT2)
        );
        runBEs(backends, ServerType.NIO2);
        log.info("Listening on port: {}", FE_PORT);
        new TcpLoadBalancer(FE_PORT, backends).start();
    }

    private static void runBEs(List<InetSocketAddress> backends, ServerType type) {
        backends.forEach(be -> {
            log.info("Starting backend server on port: {}", be.getPort());
            new Thread(() -> {
                switch (type) {
                    case NIO:
                        new NIOSocketServer(be.getPort()).start();
                        break;
                    case NIO2:
                        new NIO2AsyncServer(be.getPort()).start();
                        break;
                }
            }).start();
        });
    }

    private enum ServerType {
        NIO, NIO2
    }
}
