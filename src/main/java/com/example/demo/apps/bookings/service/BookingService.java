package com.example.demo.apps.bookings.service;

import com.example.demo.apps.bookings.entity.Booking;
import com.example.demo.apps.bookings.entity.Feature;
import com.example.demo.apps.bookings.entity.Room;

import java.util.List;
import java.util.Set;

public interface BookingService {

    void addRoom(String name,
                 int capacity, Set<Feature> features,
                 int openHour, int closeHour);

    void updateRoom(String name,
                    int newCapacity, Set<Feature> newFeatures,
                    int newOpenHour, int newCloseHour);

    Room getRoom(String roomName);

    void deleteRoom(String name);

    Booking book(int start, int end, int personCount, Set<Feature> features);

    List<Booking> getRoomSchedule(String roomName);
}
