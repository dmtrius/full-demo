package com.example.demo.apps.sockets;


import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Slf4j
public class SimpleSocketClient {
    private static final int PORT = SimpleSocketServer.PORT;
    @SuppressWarnings("unused")
    public static void main(String[] args) {
        try {
            // Connect to server at localhost:PORT
            Socket socket = new Socket("localhost", PORT);
            System.out.println("Connected to server");

            // Set up input and output streams
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Send a message to server
            out.println("Hello from client!");

            // Read response from server
            String response = in.readLine();
            System.out.println("Server response: " + response);

            // Close connections
            in.close();
            out.close();
            socket.close();

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}