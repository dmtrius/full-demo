package com.example.demo.apps.bookings.entity;

import java.util.Set;

public record Booking(int start, int end, int personCount, Set<Feature> features, Room room) {
}
