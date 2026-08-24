package com.example.demo.apps.lb;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public final class NioTcpLoadBalancerUpdated implements AutoCloseable {
    private static final int BUF_SIZE = 16 * 1024;
    private static final int MAX_PENDING_PER_SIDE = 256;
    private static final int CONNECT_TIMEOUT_MS = 3000;
    private static final int SELECT_TIMEOUT_MS = 1000;

    private final ServerSocketChannel server;
    private final Selector selector;
    private final List<InetSocketAddress> backends;
    private final AtomicInteger rr = new AtomicInteger();
    private final BufferPool pool = new BufferPool(1024, BUF_SIZE);
    private final Map<SocketChannel, Connection> connections = new HashMap<>();
    private volatile boolean running = true;

    public NioTcpLoadBalancerUpdated(int listenPort, List<InetSocketAddress> backends) throws IOException {
        if (backends == null || backends.isEmpty()) throw new IllegalArgumentException("backends required");
        this.backends = List.copyOf(backends);
        this.selector = SelectorProvider.provider().openSelector();
        this.server = ServerSocketChannel.open();
        server.configureBlocking(false);
        server.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        server.bind(new InetSocketAddress(listenPort));
        server.register(selector, SelectionKey.OP_ACCEPT);
    }

    public void start() throws IOException {
        while (running) {
            selector.select(SELECT_TIMEOUT_MS);
            processSelectedKeys();
            sweepExpiredConnections();
        }
    }

    private void processSelectedKeys() throws IOException {
        Iterator<SelectionKey> it = selector.selectedKeys().iterator();
        while (it.hasNext()) {
            SelectionKey key = it.next();
            it.remove();

            if (!key.isValid()) continue;

            if (key.isAcceptable()) {
                accept();
            } else {
                if (key.isConnectable()) {
                    finishConnect(key);
                }
                if (key.isReadable()) {
                    readReady(key);
                }
                if (key.isWritable()) {
                    writeReady(key);
                }
            }
        }
    }

    private void accept() throws IOException {
        SocketChannel client;
        while ((client = server.accept()) != null) {
            client.configureBlocking(false);
            client.setOption(StandardSocketOptions.TCP_NODELAY, true);
            client.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

            InetSocketAddress backendAddr = pickBackend();
            try (SocketChannel backend = SocketChannel.open()) {
                backend.configureBlocking(false);
                backend.setOption(StandardSocketOptions.TCP_NODELAY, true);
                backend.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

                Connection c = new Connection(client, backend, pool.borrow(), pool.borrow(), System.nanoTime());
                connections.put(client, c);
                connections.put(backend, c);

                if (!backend.connect(backendAddr)) {
                    backend.register(selector, SelectionKey.OP_CONNECT, c);
                } else {
                    backend.register(selector, SelectionKey.OP_READ, c);
                }
                client.register(selector, SelectionKey.OP_READ, c);
            }
        }
    }

    private void finishConnect(SelectionKey key) throws IOException {
        try (SocketChannel ch = (SocketChannel) key.channel()) {
            Connection c = (Connection) key.attachment();
            if (ch.finishConnect()) {
                key.interestOps(SelectionKey.OP_READ | (c.pendingToBackendHasData() ? SelectionKey.OP_WRITE : 0));
            }
        }
    }

    private void readReady(SelectionKey key) throws IOException {
        SocketChannel src = (SocketChannel) key.channel();
        Connection c = (Connection) key.attachment();
        SocketChannel dst = (src == c.client) ? c.backend : c.client;

        ByteBuffer buf = pool.borrow();
        int n = src.read(buf);

        if (n == -1) {
            c.markInputClosed(src);
            tryHalfClose(c, src);
            pool.recycle(buf);
            return;
        }

        if (n == 0) {
            pool.recycle(buf);
            return;
        }

        buf.flip();
        if (!c.enqueue(dst, buf)) {
            pool.recycle(buf);
            closeConnection(c);
            return;
        }

        SelectionKey dstKey = dst.keyFor(selector);
        if (dstKey != null && dstKey.isValid()) {
            dstKey.interestOps(dstKey.interestOps() | SelectionKey.OP_WRITE);
        }
    }

    private void writeReady(SelectionKey key) throws IOException {
        SocketChannel ch = (SocketChannel) key.channel();
        Connection c = (Connection) key.attachment();

        Deque<ByteBuffer> q = c.queueFor(ch);
        while (q != null && !q.isEmpty()) {
            ByteBuffer buf = q.peek();
            ch.write(buf);
            if (buf.hasRemaining()) return;
            q.poll();
            pool.recycle(buf);
        }

        key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
        if (c.isFullyClosed()) {
            closeConnection(c);
        }
    }

    @SuppressWarnings("java:S108")
    private void tryHalfClose(Connection c, SocketChannel closedSide) {
        try {
            SocketChannel peer = (closedSide == c.client) ? c.backend : c.client;
            if (peer.isOpen()) {
                peer.shutdownOutput();
            }
        } catch (IOException _) {
        }
    }

    private InetSocketAddress pickBackend() {
        return backends.get(Math.floorMod(rr.getAndIncrement(), backends.size()));
    }

    private void closeConnection(Connection c) {
        c.closeQuietly();
        connections.remove(c.client);
        connections.remove(c.backend);
        pool.recycle(c.clientScratch);
        pool.recycle(c.backendScratch);
    }

    private void sweepExpiredConnections() {
        long now = System.nanoTime();
        long timeoutNanos = 30L * 60L * 1_000_000_000L;
        for (Connection c : new ArrayList<>(new HashSet<>(connections.values()))) {
            if (now - c.lastActivityNanos > timeoutNanos) {
                closeConnection(c);
            }
        }
    }

    @Override
    public void close() throws IOException {
        running = false;
        selector.wakeup();
        for (Connection c : new ArrayList<>(new HashSet<>(connections.values()))) {
            closeConnection(c);
        }
        server.close();
        selector.close();
    }

    private static final class Connection {
        final SocketChannel client;
        final SocketChannel backend;
        final ByteBuffer clientScratch;
        final ByteBuffer backendScratch;
        final Map<SocketChannel, Deque<ByteBuffer>> pending = new HashMap<>();
        volatile boolean clientInputClosed;
        volatile boolean backendInputClosed;
        volatile long lastActivityNanos;

        Connection(SocketChannel client, SocketChannel backend, ByteBuffer clientScratch, ByteBuffer backendScratch, long now) {
            this.client = client;
            this.backend = backend;
            this.clientScratch = clientScratch;
            this.backendScratch = backendScratch;
            this.lastActivityNanos = now;
            pending.put(client, new ArrayDeque<>());
            pending.put(backend, new ArrayDeque<>());
        }

        boolean enqueue(SocketChannel target, ByteBuffer buf) {
            Deque<ByteBuffer> q = pending.get(target);
            if (q.size() >= MAX_PENDING_PER_SIDE) return false;
            q.add(buf);
            lastActivityNanos = System.nanoTime();
            return true;
        }

        Deque<ByteBuffer> queueFor(SocketChannel ch) {
            return pending.get(ch);
        }

        void markInputClosed(SocketChannel side) {
            if (side == client) {
                clientInputClosed = true;
            } else {
                backendInputClosed = true;
            }
            lastActivityNanos = System.nanoTime();
        }

        boolean pendingToBackendHasData() {
            return !pending.get(backend).isEmpty();
        }

        boolean isFullyClosed() {
            return (!client.isOpen() && !backend.isOpen()) || (clientInputClosed && backendInputClosed);
        }

        @SuppressWarnings("java:S108")
        void closeQuietly() {
            try {
                client.close();
            } catch (IOException _) {
            }
            try {
                backend.close();
            } catch (IOException _) {
            }
        }
    }

    private static final class BufferPool {
        private final ConcurrentLinkedQueue<ByteBuffer> q = new ConcurrentLinkedQueue<>();
        private final int bufSize;

        BufferPool(int initial, int bufSize) {
            this.bufSize = bufSize;
            for (int i = 0; i < initial; i++) q.offer(ByteBuffer.allocateDirect(bufSize));
        }

        ByteBuffer borrow() {
            ByteBuffer buf = q.poll();
            return (buf != null) ? buf.clear() : ByteBuffer.allocateDirect(bufSize);
        }

        void recycle(ByteBuffer buf) {
            if (buf == null) return;
            buf.clear();
            q.offer(buf);
        }
    }

    static void main() throws Exception {
        var backends = List.of(
            new InetSocketAddress("127.0.0.1", 8001),
            new InetSocketAddress("127.0.0.1", 8002)
        );
        try (NioTcpLoadBalancerUpdated lb = new NioTcpLoadBalancerUpdated(9000, backends)) {
            lb.start();
        }
    }
}
