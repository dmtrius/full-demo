package com.example.demo.apps.tasks;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

@Slf4j
public class OpenNotifyExample {
    void main() {
        try {
            String endpoint = "https://api.open-notify.org/iss-now.json";
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
                IO.println("ISS Location: " + response);

//                JSONObject json = new JSONObject(response);
//                JSONObject issPosition = json.getJSONObject("iss_position");
//                double latitude = issPosition.getDouble("latitude");
//                double longitude = issPosition.getDouble("longitude");
//                System.out.printf("ISS is at: Latitude %.4f, Longitude %.4f%n", latitude, longitude);
                  testRuntime2(response.toString());
            } else {
                IO.println("Error: HTTP " + responseCode);
            }
            conn.disconnect();
        } catch (URISyntaxException | IOException e) {
            log.error("Error occurred", e);
            throw new RuntimeException(e);
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
            IO.println("Exit Code: " + exitCode);
            IO.println("Output:\n" + output);
            if (!error.isEmpty()) {
                log.error("Errors: {}", error);
            }

        } catch (Exception e) {
            log.error("Error executing jq command", e);
        }
    }

    @SuppressWarnings("unused")
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
            IO.println("Exit Code: " + exitCode);
            IO.println("Output:\n" + output);

            // Optionally, capture error stream
            BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()));
            StringBuilder error = new StringBuilder();
            while ((line = errorReader.readLine()) != null) {
                error.append(line).append("\n");
            }
            if (!error.isEmpty()) {
                log.error("Errors: {}", error);
            }

        } catch (Exception e) {
            log.error("Error executing jq command", e);
        }
    }
}
