package com.example.demo.apps.update;

import lombok.Data;

@Data
public class JudgeEventResponse {
    String id;
    String title;
    Long start;
    Long end;
    Long createdOn;
    EventCategoryType categoryType;
    String statusType;
}
