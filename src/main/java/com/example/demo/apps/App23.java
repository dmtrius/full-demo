package com.example.demo.apps;

import com.example.demo.apps.update.EventCategoryType;
import com.example.demo.apps.update.EventStatusType;
import com.example.demo.apps.update.JudgeEventRequest;
import com.example.demo.apps.update.JudgeEventResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.WeakHashMap;

public class App23 {
    void main() {
//        System.out.println(getJudgesList());
//        System.out.println(findJudge("Bertila"));
//        System.out.println(getCalendar("27334", EventCategoryType.MC));
        System.out.println(List.of());
        System.out.println(Collections.emptyList());
    }

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String uri = "https://cmap.jud11.flcourts.org/jisws/jisService/v100/schedulingDivisions";
    private static final String calendarUri = "https://cmap.jud11.flcourts.org/jisws/jisService/v100/plans";
    private static WeakHashMap<Integer, Judge> judgesList = new WeakHashMap<>();

    public static Judges getJudgesList() {
        if (!judgesList.isEmpty()) {
            Judges list = new Judges();
            list.addAll(judgesList.values());
            return list;
        }
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>("{}", headers);
        ResponseEntity<Judges> result =
                restTemplate.exchange(uri, HttpMethod.POST, entity, Judges.class);

        if (result.hasBody()) {
            updateCache(Objects.requireNonNull(result.getBody()));
            return result.getBody();
        } else {
            return new Judges();
        }
    }

    private static void updateCache(Judges list) {
        list.forEach(j -> judgesList.put(j.getId(), j));
    }

    public static Judges findJudge(String name) {
        getJudgesList();
        Judges result = new Judges();
        judgesList.forEach((id,judge) -> {
            if (judge.getLabel().toLowerCase().contains(name.toLowerCase())) {
                result.add(judge);
            }
        });
        return result;
    }

    @SneakyThrows
    public static List<JudgeEventResponse> getCalendar(String id, EventCategoryType categoryType) {
        JudgeEventRequest request = new JudgeEventRequest();
        JudgeEventRequest.Division division = new JudgeEventRequest.Division();
        division.setId(id);
        request.setDivision(division);
        request.setCategoryType(categoryType);
        request.setStatusType(EventStatusType.AVAILABLE);

        request.setStart(Instant.now().toEpochMilli());
        request.setEnd(Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli());

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<String> entity = new HttpEntity<>(jsonRequest, headers);
        ResponseEntity<List<JudgeEventResponse>> result =
                restTemplate.exchange(calendarUri, HttpMethod.POST, entity, new ParameterizedTypeReference<>() {
                });

        return result.hasBody() ? result.getBody() : List.of();
    }
}

class Judges extends ArrayList<Judge> {

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

