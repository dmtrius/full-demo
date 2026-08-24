package com.example.demo.apps.lb;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class NioTcpLoadBalancer {
    private final ServerSocketChannel server;
    private final Selector selector;
    private final List<InetSocketAddress> backends;
    private int rr = 0;
    private AtomicBoolean isRunning = new AtomicBoolean(true);

    public NioTcpLoadBalancer(int listenPort, List<InetSocketAddress> backends) throws IOException {
        this.backends = backends;
        this.selector = Selector.open();
        this.server = ServerSocketChannel.open();
        this.server.configureBlocking(false);
        this.server.bind(new InetSocketAddress(listenPort));
        this.server.register(selector, SelectionKey.OP_ACCEPT);
    }

    public void start() {
        try {
            while (isRunning.get()) {
                log.info(selector.toString());
                int keysN = selector.select();
                log.info("number of keys: {}", keysN);
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();

                    if (!key.isValid()) {
                        continue;
                    }

                    if (key.isAcceptable()) {
                        accept();
                    } else if (key.isConnectable()) {
                        finishConnect(key);
                    } else if (key.isReadable()) {
                        readAndForward(key);
                    } else if (key.isWritable()) {
                        writePending(key);
                    }
                }
            }
        } catch (IOException e) {
            log.error("Error in load balancer loop: {}", e.getMessage());
        }
    }

    private void accept() {
        try {
            SocketChannel client = server.accept();
            if (client == null) {
                log.info("No client accepted.");
                return;
            }
            client.configureBlocking(false);
            client.setOption(StandardSocketOptions.TCP_NODELAY, true);

            try (SocketChannel backend = SocketChannel.open()) {
                backend.configureBlocking(false);
                backend.setOption(StandardSocketOptions.TCP_NODELAY, true);

                InetSocketAddress target = pickBackend();
                backend.connect(target);

                ConnectionState state = new ConnectionState(client, backend);

                client.register(selector, SelectionKey.OP_READ, state);
                backend.register(selector, SelectionKey.OP_CONNECT, state);
            }
        } catch (IOException e) {
            log.error("Error accepting connection: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void finishConnect(SelectionKey key) {
        try (SocketChannel ch = (SocketChannel) key.channel()) {
            if (ch.finishConnect()) {
                key.interestOps(SelectionKey.OP_READ);
            }
        } catch (IOException e) {
            log.error("Error finishing connection: {}", e.getMessage());
        }
    }

    private void readAndForward(SelectionKey key) {
        try {
            SocketChannel src = (SocketChannel) key.channel();
            ConnectionState state = (ConnectionState) key.attachment();
            SocketChannel dst = (src == state.client) ? state.backend : state.client;

            ByteBuffer buf = ByteBuffer.allocate(8192);
            int n = src.read(buf);

            if (n == -1) {
                close(state);
                return;
            }
            if (n == 0) {
                return;
            }

            buf.flip();
            state.queue(dst, buf);
            SelectionKey dstKey = dst.keyFor(selector);
            if (dstKey != null && dstKey.isValid()) {
                dstKey.interestOps(dstKey.interestOps() | SelectionKey.OP_WRITE);
            }
        } catch (IOException e) {
            log.error("Error reading and forwarding: {}", e.getMessage());
        }
    }

    private void writePending(SelectionKey key) {
        try {
            SocketChannel ch = (SocketChannel) key.channel();
            ConnectionState state = (ConnectionState) key.attachment();
            Deque<ByteBuffer> q = state.pending.get(ch);

            while (q != null && !q.isEmpty()) {
                ByteBuffer buf = q.peek();
                ch.write(buf);
                if (buf.hasRemaining()) break;
                q.poll();
            }

            if (q == null || q.isEmpty()) {
                key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
            }
        } catch (IOException e) {
            log.error("Error writing pending data: {}", e.getMessage());
        }
    }

    private InetSocketAddress pickBackend() {
        return backends.get(Math.floorMod(rr++, backends.size()));
    }

    @SuppressWarnings("java:S108")
    private void close(ConnectionState state) {
        try {
            state.client.close();
            state.backend.close();
        } catch (IOException _) {}
    }

    private static class ConnectionState {
        final SocketChannel client;
        final SocketChannel backend;
        final Map<SocketChannel, Deque<ByteBuffer>> pending = new HashMap<>();

        ConnectionState(SocketChannel client, SocketChannel backend) {
            this.client = client;
            this.backend = backend;
            pending.put(client, new ArrayDeque<>());
            pending.put(backend, new ArrayDeque<>());
        }

        void queue(SocketChannel target, ByteBuffer buf) {
            pending.get(target).add(buf);
        }
    }

    private static final int PORT = 9000;
    private static final int BE_PORT1 = 8001;
    private static final int BE_PORT2 = 8002;

    static void main() {
        var backends = List.of(
            new InetSocketAddress("127.0.0.1", BE_PORT1),
            new InetSocketAddress("127.0.0.1", BE_PORT2)
        );
        try {
            log.info("Running on port: " + PORT);
            new NioTcpLoadBalancer(PORT, backends).start();
        } catch (IOException e) {
            log.error("Error starting load balancer: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
