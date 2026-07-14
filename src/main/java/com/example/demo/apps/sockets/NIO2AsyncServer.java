package com.example.demo.apps.sockets;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NIO2AsyncServer {
    void main() {
        try {
            // Create a thread pool for the channel group
            AsynchronousChannelGroup group = AsynchronousChannelGroup.withThreadPool(
                    Executors.newFixedThreadPool(10));

            // Create the asynchronous server socket channel
            AsynchronousServerSocketChannel serverChannel = AsynchronousServerSocketChannel
                    .open(group)
                    .bind(new InetSocketAddress("localhost", 8888));

            // Configure socket options
            serverChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);

            IO.println("NIO.2 Asynchronous server started on port 8888");

            // Start accepting connections asynchronously
            serverChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
                @Override
                public void completed(AsynchronousSocketChannel clientChannel, Void attachment) {
                    // Accept the next connection
                    serverChannel.accept(null, this);

                    try {
                        IO.println("Client connected: " + clientChannel.getRemoteAddress());

                        // Allocate buffer for reading client data
                        ByteBuffer buffer = ByteBuffer.allocate(1024);

                        // Read data asynchronously
                        clientChannel.read(buffer, buffer, new CompletionHandler<>() {
                            @Override
                            public void completed(Integer bytesRead, ByteBuffer buffer) {
                                if (bytesRead > 0) {
                                    buffer.flip();
                                    byte[] data = new byte[buffer.limit()];
                                    buffer.get(data);
                                    String message = new String(data);
                                    IO.println("Received: " + message.trim());

                                    // Echo the message back to client
                                    ByteBuffer writeBuffer = ByteBuffer.wrap(("Echo: " + message).getBytes());
                                    clientChannel.write(writeBuffer, writeBuffer, new CompletionHandler<>() {
                                        @Override
                                        public void completed(Integer bytesWritten, ByteBuffer buffer) {
                                            // Read next message from the client
                                            if (buffer.hasRemaining()) {
                                                clientChannel.write(buffer, buffer, this);
                                            } else {
                                                ByteBuffer newBuffer = ByteBuffer.allocate(1024);
                                                clientChannel.read(newBuffer, newBuffer,
                                                    new ReadCompletionHandler(clientChannel));
                                            }
                                        }

                                        @Override
                                        public void failed(Throwable exc, ByteBuffer buffer) {
                                            log.error("Write failed: {}", exc.getMessage(), exc);
                                            closeChannel(clientChannel);
                                        }
                                    });
                                } else {
                                    // Client closed the connection
                                    closeChannel(clientChannel);
                                }
                            }

                            @Override
                            public void failed(Throwable exc, ByteBuffer buffer) {
                                log.error("Read failed: {}", exc.getMessage(), exc);
                                closeChannel(clientChannel);
                            }
                        });

                    } catch (IOException e) {
                        log.error("Client handling exception: {}", e.getMessage(), e);
                        closeChannel(clientChannel);
                    }
                }

                @Override
                public void failed(Throwable exc, Void attachment) {
                    log.error("Accept failed: {}", exc.getMessage(), exc);
                }
            });

            // Keep the server running
            boolean result = group.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
            if (!result) {
                log.info("Terminated timeout");
            }

        } catch (IOException | InterruptedException e) {
            log.error("Server exception: {}", e.getMessage(), e);
        }
    }

    // Inner class for continuous reading from client
    @Slf4j
    private static class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
        private final AsynchronousSocketChannel clientChannel;

        ReadCompletionHandler(AsynchronousSocketChannel clientChannel) {
            this.clientChannel = clientChannel;
        }

        @Override
        public void completed(Integer bytesRead, ByteBuffer buffer) {
            if (bytesRead > 0) {
                buffer.flip();
                byte[] data = new byte[buffer.limit()];
                buffer.get(data);
                String message = new String(data);
                IO.println("Received: " + message.trim());

                // Echo the message back to client
                ByteBuffer writeBuffer = ByteBuffer.wrap(("Echo: " + message).getBytes());
                clientChannel.write(writeBuffer, writeBuffer, new CompletionHandler<>() {
                    @Override
                    public void completed(Integer bytesWritten, ByteBuffer buffer) {
                        if (buffer.hasRemaining()) {
                            clientChannel.write(buffer, buffer, this);
                        } else {
                            ByteBuffer newBuffer = ByteBuffer.allocate(1024);
                            clientChannel.read(newBuffer, newBuffer, ReadCompletionHandler.this);
                        }
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer buffer) {
                        log.error("Write failed: {}", exc.getMessage(), exc);
                        closeChannel(clientChannel);
                    }
                });
            } else {
                // Client closed the connection
                closeChannel(clientChannel);
            }
        }

        @Override
        public void failed(Throwable exc, ByteBuffer buffer) {
            log.error("Read failed: {}", exc.getMessage(), exc);
            closeChannel(clientChannel);
        }
    }

    private static void closeChannel(AsynchronousSocketChannel channel) {
        try {
            IO.println("Closing connection with client: " + channel.getRemoteAddress());
            channel.close();
        } catch (IOException e) {
            log.error("Error closing channel: {}", e.getMessage(), e);
        }
    }
}
