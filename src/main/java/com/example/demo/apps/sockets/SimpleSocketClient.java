package com.example.demo.apps.sockets;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SimpleSocketClient {
    public static void main(String[] args) {
        try {
            // Connect to server at localhost:8888
            Socket socket = new Socket("localhost", 8888);
            System.out.println("Connected to server");

            // Set up input and output streams
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Send message to server
            out.println("Hello from client!");

            // Read response from server
            String response = in.readLine();
            System.out.println("Server response: " + response);

            // Close connections
            in.close();
            out.close();
            socket.close();

        } catch (IOException e) {
            System.out.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}