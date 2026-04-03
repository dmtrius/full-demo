package com.example.demo.apps;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static java.lang.IO.println;

public class SimpleHttpServer {
    void main() throws IOException {
        // Create an HttpServer instance
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        // Create a context for handling requests at the root path
        server.createContext("/", new MyHandler());

        // Start the server
        server.setExecutor(null); // Use the default executor
        server.start();

        println("Server is running on port 8000");
    }

    // Define a custom HttpHandler
    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String request = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            println("Received request: " + request);

            // Handle the request
            String response = "Hello, this is a simple HTTP server response!";
            exchange.sendResponseHeaders(200, response.length()); // Send HTTP status code and response length

            OutputStream os = exchange.getResponseBody(); // Get output stream
            os.write(response.getBytes()); // Write response to output stream
            os.close(); // Close the output stream
        }
    }
}

