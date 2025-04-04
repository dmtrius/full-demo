package com.example.demo.apps.update;

import lombok.Data;

@Data
public class JudgeEventRequest {
    Division division;
    EventCategoryType categoryType;
    EventStatusType statusType;
    Long start;
    Long end;

    @Data
    public static class Division {
        String id;
    }
}
