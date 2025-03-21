package com.example.demo.apps.sockets;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Scanner;

@Slf4j
public class NIO2AsyncClient {

    private static final String LOCALHOST = "localhost";
    private static final int PORT = 8888;
    private static final int CAPACITY = 1024;
    private static final String EXIT = "quit";

    @SuppressWarnings("unused")
    public static void main(String... args) {
        try {
            // Create asynchronous socket channel
            AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();

            // Configure socket options
            socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
            socketChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);

            // Connect to server asynchronously
            socketChannel.connect(new InetSocketAddress(LOCALHOST, PORT), socketChannel,
                    new CompletionHandler<>() {
                        @Override
                        public void completed(Void result, AsynchronousSocketChannel channel) {
                            try {
                                log.info("Connected to server: {}", channel.getRemoteAddress());

                                // Start user input thread
                                startClientInputProcess(channel);

                                // Start reading responses from server
                                ByteBuffer buffer = ByteBuffer.allocate(CAPACITY);
                                startReading(channel, buffer);
                            } catch (IOException e) {
                                log.error("Client connection exception: {}", e.getMessage());
                            }
                        }

                        @Override
                        public void failed(Throwable exc, AsynchronousSocketChannel channel) {
                            log.info("Connection failed: {}", exc.getMessage());
                            closeChannel(channel);
                        }
                    });
            // Wait for user to enter "exit"
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String input = scanner.nextLine();
                if (EXIT.equalsIgnoreCase(input)) {
                    break;
                }
            }

        } catch (IOException e) {
            log.error("Client exception: {}", e.getMessage());
        }
    }

    private static void startClientInputProcess(AsynchronousSocketChannel channel) {
        new Thread(() -> {
            try (Scanner scanner = new Scanner(System.in)) {
                System.out.println("Enter messages to send to server (type '" + EXIT + "' to disconnect): ");

                while (true) {
                    String message = scanner.nextLine();

                    if (EXIT.equalsIgnoreCase(message)) {
                        closeChannel(channel);
                        break;
                    }

                    ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());

                    // Write to server asynchronously
                    channel.write(buffer, buffer, new CompletionHandler<>() {
                        @Override
                        public void completed(Integer bytesWritten, ByteBuffer buffer) {
                            if (buffer.hasRemaining()) {
                                channel.write(buffer, buffer, this);
                            }
                        }

                        @Override
                        public void failed(Throwable exc, ByteBuffer buffer) {
                            log.info("Failed to send message: {}", exc.getMessage());
                            closeChannel(channel);
                        }
                    });
                }
            }
        }).start();
    }

    private static void startReading(AsynchronousSocketChannel channel, ByteBuffer buffer) {
        channel.read(buffer, buffer, new CompletionHandler<>() {
            @Override
            public void completed(Integer bytesRead, ByteBuffer buffer) {
                if (bytesRead > 0) {
                    buffer.flip();
                    byte[] data = new byte[buffer.limit()];
                    buffer.get(data);
                    log.info("Server: {}", new String(data));
                    buffer.clear();
                    channel.read(buffer, buffer, this);
                } else {
                    log.info("Server closed the connection");
                    closeChannel(channel);
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer buffer) {
                log.info("Read failed: {}", exc.getMessage());
                closeChannel(channel);
            }
        });
    }

    private static void closeChannel(AsynchronousSocketChannel channel) {
        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
                log.info("Disconnected from server");
            }
        } catch (IOException e) {
            log.error("Error closing channel: {}", e.getMessage());
        }
    }
}