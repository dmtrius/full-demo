package com.example.demo.apps.sockets;

import java.io.*;
import java.net.*;

public class MultithreadedSocketServer {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8888);
            System.out.println("Multithreaded server started. Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());

                // Create a new thread for each client
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;

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
                System.out.println("Received from client " + clientSocket.getInetAddress().getHostAddress() + ": " + inputLine);

                // Echo the message back to client
                out.println("Server echoes: " + inputLine);

                // If client sends "bye", close the connection
                if ("bye".equalsIgnoreCase(inputLine)) {
                    break;
                }
            }

            in.close();
            out.close();
            clientSocket.close();
            System.out.println("Client disconnected: " + clientSocket.getInetAddress().getHostAddress());

        } catch (IOException e) {
            System.out.println("Client handler exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}