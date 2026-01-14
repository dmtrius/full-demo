package com.example.demo.apps.bookings.entity;

import java.util.Set;

public record Room(
        String name,
        int capacity,
        Set<Feature> features,
        int openHour,
        int closeHour
) {
}