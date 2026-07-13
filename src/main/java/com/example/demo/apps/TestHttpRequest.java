package com.example.demo.apps;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

@Slf4j
public class TestHttpRequest {
    void main() {
        try {
            // Create a URL object
            URI uri = new URI("https://jsonplaceholder.typicode.com/posts/1");
            URL url = uri.toURL();

            // Open a connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to GET
            connection.setRequestMethod("GET");

            // Get the response code
            int responseCode = connection.getResponseCode();
            IO.println("Response Code: " + responseCode);

            // Read the response
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Print the response
            IO.println("Response: " + response);

        } catch (Exception e) {
            log.error("Error occurred while making HTTP request", e);
        }
    }
}
