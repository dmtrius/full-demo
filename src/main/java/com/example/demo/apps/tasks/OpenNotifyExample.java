package com.example.demo.apps.tasks;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class OpenNotifyExample {
    public static void main(String[] args) {
        try {
            String endpoint = "http://api.open-notify.org/iss-now.json";
            URL url = new URI(endpoint).toURL();
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
                System.out.println("ISS Location: " + response);

//                JSONObject json = new JSONObject(response);
//                JSONObject issPosition = json.getJSONObject("iss_position");
//                double latitude = issPosition.getDouble("latitude");
//                double longitude = issPosition.getDouble("longitude");
//                System.out.printf("ISS is at: Latitude %.4f, Longitude %.4f%n", latitude, longitude);
                  testRuntime2(response.toString());
            } else {
                System.out.println("Error: HTTP " + responseCode);
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void testRuntime2(String data) {
        try {
            // JSON input to pass to jq

            // jq command (e.g., extract the 'name' field)
            String jqCommand = "jq -r .";

            // Execute the command
            Process process = Runtime.getRuntime().exec(jqCommand);

            // Pass input to jq via stdin
            OutputStreamWriter writer = new OutputStreamWriter(process.getOutputStream());
            writer.write(data);
            writer.flush();
            writer.close(); // Close to signal EOF to jq

            // Capture the output
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // Capture errors (if any)
            BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()));
            StringBuilder error = new StringBuilder();
            while ((line = errorReader.readLine()) != null) {
                error.append(line).append("\n");
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();
            System.out.println("Exit Code: " + exitCode);
            System.out.println("Output:\n" + output);
            if (!error.isEmpty()) {
                System.err.println("Errors:\n" + error);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void testRuntime(String data) {
        try {
            // Execute the command
            Process process = Runtime.getRuntime().exec("jq -r . <<< '" + data + "'");

            // Capture the output
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();
            System.out.println("Exit Code: " + exitCode);
            System.out.println("Output:\n" + output);

            // Optionally, capture error stream
            BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()));
            StringBuilder error = new StringBuilder();
            while ((line = errorReader.readLine()) != null) {
                error.append(line).append("\n");
            }
            if (!error.isEmpty()) {
                System.err.println("Errors:\n" + error);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
