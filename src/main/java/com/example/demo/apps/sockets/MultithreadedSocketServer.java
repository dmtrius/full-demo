package com.example.demo.apps.sockets;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.*;

@Slf4j
public class MultithreadedSocketServer {
    public static final int PORT = 8888;

    @SuppressWarnings("unused")
    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(PORT)
        ) {
            log.info("Multithreaded server started. Waiting for clients...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                log.info("New client connected: {}", clientSocket.getInetAddress().getHostAddress());
                // Create a new thread for each client
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            log.error("Server exception: {}", e.getMessage());
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
        try(PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                log.info("Received from client {}: {}", clientSocket.getInetAddress().getHostAddress(), inputLine);
                // Echo the message back to client
                out.println("Server echoes: " + inputLine);
                // If client sends "bye", close the connection
                if ("bye".equalsIgnoreCase(inputLine)) {
                    break;
                }
            }
            clientSocket.close();
            log.info("Client disconnected: {}", clientSocket.getInetAddress().getHostAddress());
        } catch (IOException e) {
            log.error("Client handler exception: {}", e.getMessage());
        }
    }
}