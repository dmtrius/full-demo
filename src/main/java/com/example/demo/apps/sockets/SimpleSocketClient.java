package com.example.demo.apps.sockets;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Slf4j
public class SimpleSocketClient {
    public static final int PORT = 8888;

    @SuppressWarnings("unused")
    public static void main(String... args) {
        try (Socket socket = new Socket("localhost", PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            log.info("Connected to server");
            out.println("Hello from client!");
            String response = in.readLine();
            log.info("Server response: {}", response);
        } catch (IOException e) {
            log.error("Exception occurred: {}", e.getMessage());
        }
    }
}