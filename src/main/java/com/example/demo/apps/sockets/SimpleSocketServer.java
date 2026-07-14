package com.example.demo.apps.sockets;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.*;

@Slf4j
public class SimpleSocketServer {
    public static final int PORT = 8888;
    void main() {
        try {
            // Create server socket
            ServerSocket serverSocket = new ServerSocket(PORT);
            IO.println("Server started. Waiting for client connection...");

            // Wait for client connection
            Socket clientSocket = serverSocket.accept();
            IO.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

            // Set up input and output streams
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Read a message from the client
            String inputLine = in.readLine();
            IO.println("Received from client: " + inputLine);

            // Send the response to a client
            IO.println("Hello from server! I received: " + inputLine);

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