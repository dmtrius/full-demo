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
        List<Judge> result = get();
        System.out.println(result.getFirst());
        System.out.println("___");
        System.out.println(result);
    }

    private static List<Judge> get() {
        RestTemplate restTemplate = new RestTemplate();

        String uri = "https://cmap.jud11.flcourts.org/jisws/jisService/v100/schedulingDivisions";

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        HttpEntity<String> entity = new HttpEntity<>("{}", headers);
        ResponseEntity<Judges> result =
                restTemplate.exchange(uri, HttpMethod.POST, entity, Judges.class);
        return result.hasBody() && result.getBody() != null ? result.getBody() : Collections.emptyList();
    }
}

class Judges extends ArrayList<Judge> {}

@Data
class Judge {
    private int id;
    private String name;
    private String label;
    private String description;
    private String judicialOfficerType;
    private String jurisdiction;
}
