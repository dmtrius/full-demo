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
    void main() {
        try {
            // Create asynchronous socket channel
            AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();

            // Configure socket options
            socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
            socketChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);

            // Connect to server asynchronously
            socketChannel.connect(new InetSocketAddress("localhost", 8888), socketChannel,
                new CompletionHandler<>() {
                    @Override
                    public void completed(Void result, AsynchronousSocketChannel channel) {
                        try {
                            IO.println("Connected to server: " + channel.getRemoteAddress());

                            // Start user input thread
                            startClientInputProcess(channel);

                            // Start reading responses from server
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            startReading(channel, buffer);

                        } catch (IOException e) {
                            log.error("Client connection exception: {}", e.getMessage());
                            closeChannel(channel);
                        }
                    }

                    @Override
                    public void failed(Throwable exc, AsynchronousSocketChannel channel) {
                        System.out.println("Connection failed: " + exc.getMessage());
                        closeChannel(channel);
                    }
                });

            // Wait for user to enter "exit"
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String input = scanner.nextLine();
                if ("exit".equalsIgnoreCase(input)) {
                    break;
                }
            }

        } catch (IOException e) {
            log.error("Client exception: {}", e.getMessage(), e);
        }
    }

    private static void startClientInputProcess(AsynchronousSocketChannel channel) {
        new Thread(() -> {
            try (Scanner scanner = new Scanner(System.in)) {
                System.out.println("Enter messages to send to server (type 'quit' to disconnect):");

                while (true) {
                    String message = scanner.nextLine();

                    if ("quit".equalsIgnoreCase(message)) {
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
                            log.error("Failed to send message: {}", exc.getMessage(), exc);
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
                    System.out.println("Server: " + new String(data));

                    buffer.clear();
                    channel.read(buffer, buffer, this);
                } else {
                    System.out.println("Server closed the connection");
                    closeChannel(channel);
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer buffer) {
                log.error("Read failed: {}", exc.getMessage(), exc);
                closeChannel(channel);
            }
        });
    }

    private static void closeChannel(AsynchronousSocketChannel channel) {
        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
                IO.println("Disconnected from server");
            }
        } catch (IOException e) {
            log.error("Error closing channel: {}", e.getMessage(), e);
        }
    }
}
