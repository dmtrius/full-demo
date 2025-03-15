package com.example.demo.apps.sockets;

import java.io.*;
import java.net.*;

public class SimpleSocketServer {
    public static void main(String[] args) {
        try {
            // Create server socket on port 8888
            ServerSocket serverSocket = new ServerSocket(8888);
            System.out.println("Server started. Waiting for client connection...");

            // Wait for client connection
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

            // Set up input and output streams
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Read message from client
            String inputLine = in.readLine();
            System.out.println("Received from client: " + inputLine);

            // Send response to client
            out.println("Hello from server! I received: " + inputLine);

            // Close connections
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();

        } catch (IOException e) {
            System.out.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}