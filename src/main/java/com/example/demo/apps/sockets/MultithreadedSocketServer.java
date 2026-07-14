package com.example.demo.apps.sockets;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.*;

@Slf4j
public class MultithreadedSocketServer {
    void main() {
        try (ServerSocket serverSocket = new ServerSocket(8888)) {
            runSocket(serverSocket);
        } catch (IOException e) {
            log.error("Could not start server: {}", e.getMessage(), e);
        }
    }

    private void runSocket(ServerSocket serverSocket) {
        try {
            IO.println("Multithreaded server started. Waiting for clients...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                IO.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());
                // Create a new thread for each client
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            log.error("Server exception: {}", e.getMessage(), e);
        }
    }
}

@Slf4j
class ClientHandler implements Runnable {
    private final Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                IO.println("Received from client " + clientSocket.getInetAddress().getHostAddress() + ": " + inputLine);

                // Echo the message back to client
                IO.println("Server echoes: " + inputLine);

                // If client sends "bye", close the connection
                if ("bye".equalsIgnoreCase(inputLine)) {
                    break;
                }
            }

            in.close();
            out.close();
            clientSocket.close();
            IO.println("Client disconnected: " + clientSocket.getInetAddress().getHostAddress());

        } catch (IOException e) {
            log.error("Client handler exception: {}", e.getMessage(), e);
        }
    }
}
