package com.example.demo.apps.tasks;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NasaApiExample {
    public static void main(String[] args) {
        try {
            String apiKey = "n4uPn1dZnJNGbHnDrKgeajiZTnhH429DvEj3PHGt"; // Replace with your NASA API key
            String endpoint = "https://api.nasa.gov/planetary/apod?api_key=" + apiKey;
            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                System.out.println("Response: " + response.toString());
            } else {
                System.out.println("Error: HTTP " + responseCode);
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
