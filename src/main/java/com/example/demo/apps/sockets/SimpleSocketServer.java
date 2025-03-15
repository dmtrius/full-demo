package com.example.demo.apps.sockets;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.*;

@Slf4j
public class SimpleSocketServer {
    public static final int PORT = 8888;

    @SuppressWarnings("unused")
    public static void main(String... args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT);
             Socket clientSocket = serverSocket.accept();
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            log.info("Server started. Waiting for client connection...");
            log.info("Client connected: {}", clientSocket.getInetAddress().getHostAddress());
            String inputLine = in.readLine();
            log.info("Received from client: {}", inputLine);
            out.println("Hello from server! I received: " + inputLine);
        } catch (IOException e) {
            log.error("Exception occurred: {}", e.getMessage());
        }
    }
}