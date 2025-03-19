package com.example.demo.apps;

import lombok.Data;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class App23 {
    void main() {
        System.out.println(get());
    }

    private static String get() {
        RestTemplate restTemplate = new RestTemplate();

        String uri = "https://cmap.jud11.flcourts.org/jisws/jisService/v100/schedulingDivisions"; // or any other uri

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");

        HttpEntity<String> entity = new HttpEntity<>("{}", headers);
        ResponseEntity<?> result =
                restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
        return result.hasBody() ? result.getBody().toString() : "";
    }
}

class Judges<Judge> extends ArrayList {

}

@Data
class Judge {
    private int id;
    private String name;
    private String label;
    private String description;
    private String judicialOfficerType;
    private String jurisdiction;
}
