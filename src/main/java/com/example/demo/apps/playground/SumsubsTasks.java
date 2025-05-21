package com.example.demo.apps.playground;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SumsubsTasks {
    void main() {
        generateAccessToker();
        createApplicant();
    }

    void createApplicant() {
        try (var httpClient = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.sumsub.com/resources/applicants?levelName=basic-level"))
                    .header("content-type", "application/json")
                    .header("X-App-Token", "sbx:LbPM4PrgIX1FxfKl4ER2kmug.WYZzAvGLdoZNYsf4Wnxi1ZeBcqLGv3jd")
                    .method("POST", HttpRequest.BodyPublishers.ofString("{\"externalUserId\":\"q-10202ss-43434\"}"))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    void generateAccessToker() {
        try (var httpClient = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.sumsub.com/resources/accessTokens/sdk"))
                    .header("content-type", "application/json")
                    .header("X-App-Token", "sbx:LbPM4PrgIX1FxfKl4ER2kmug.WYZzAvGLdoZNYsf4Wnxi1ZeBcqLGv3jd")
                    .method("POST", HttpRequest.BodyPublishers.ofString("{\"applicantIdentifiers\":{\"email\":\"string\",\"phone\":\"string\"},\"ttlInSecs\":600,\"userId\":\"sg_0001\",\"levelName\":\"basic-level\",\"externalActionId\":\"sg_action_0001\"}"))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
