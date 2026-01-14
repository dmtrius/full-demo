package com.example.demo.apps.bookings.entity;

import java.time.LocalDateTime;
import java.util.Set;

public record Booking(
    int start,
    int end,
    int personCount,
    Set<Feature> features,
//    LocalDateTime date,
    Room room) {
}
