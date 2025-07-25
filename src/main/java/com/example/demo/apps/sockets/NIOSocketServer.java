package com.example.demo.apps.sockets;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

@Slf4j
public class NIOSocketServer {
    private static final int PORT = 8888;

    @SuppressWarnings("unused")
    public static void main(String[] args) {
        try {
            // Create selector
            Selector selector = Selector.open();

            // Create and configure the server socket channel
            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.socket().bind(new InetSocketAddress(PORT));

            // Register the channel with selector, for accept operations
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("NIO server started on port " + PORT);

            ByteBuffer buffer = ByteBuffer.allocate(256);

            while (true) {
                // Select ready channels
                selector.select();

                // Get selected keys
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = selectedKeys.iterator();

                while (iter.hasNext()) {
                    SelectionKey key = iter.next();

                    if (key.isAcceptable()) {
                        // Accept new connection
                        SocketChannel client = serverChannel.accept();
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ);
                        System.out.println("New client connected: " + client.getRemoteAddress());
                    }

                    if (key.isReadable()) {
                        // Read data from a client
                        SocketChannel client = (SocketChannel) key.channel();
                        buffer.clear();
                        int bytesRead = client.read(buffer);

                        if (bytesRead == -1) {
                            // Connection closed by client
                            client.close();
                            key.cancel();
                            System.out.println("Client disconnected");
                            continue;
                        }

                        buffer.flip();
                        byte[] data = new byte[buffer.limit()];
                        buffer.get(data);
                        String message = new String(data).trim();
                        System.out.println("Received: " + message);

                        // Echo back to a client
                        buffer.clear();
                        buffer.put(("Echo: " + message).getBytes());
                        buffer.flip();
                        client.write(buffer);
                    }

                    iter.remove();
                }
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}