package com.example.demo.apps.sockets;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Slf4j
public class SimpleSocketClient {
    private final int port;

    public SimpleSocketClient(int port) {
        this.port = port;
    }

    static void main() {
        SimpleSocketClient client = new SimpleSocketClient(8888);
        client.start();
    }

    void start() {
        try {
            // Connect to server at localhost:PORT
            Socket socket = new Socket("localhost", port);
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