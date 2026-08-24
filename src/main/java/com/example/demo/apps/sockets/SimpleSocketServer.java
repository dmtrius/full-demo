package com.example.demo.apps.sockets;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.*;

@Slf4j
public class SimpleSocketServer {
    private final int port;

    public SimpleSocketServer(int port) {
        this.port = port;
    }

    static void main() {
        SimpleSocketServer server = new SimpleSocketServer(8888);
        server.start();
    }

    public void start() {
        try {
            // Create server socket
            ServerSocket serverSocket = new ServerSocket(this.port);
            log.info("Server started. Waiting for client connection...");

            // Wait for client connection
            Socket clientSocket = serverSocket.accept();
            log.info("Client connected: {}", clientSocket.getInetAddress().getHostAddress());

            // Set up input and output streams
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Read a message from the client
            String inputLine = in.readLine();
            log.info("Received from client: {}", inputLine);

            // Send the response to a client
            log.info("Sending to client: {}", inputLine);
            out.println(inputLine);

            // Close connections
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
