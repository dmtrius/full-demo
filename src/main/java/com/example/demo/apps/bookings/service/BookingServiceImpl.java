package com.example.demo.apps.bookings.service;

import com.example.demo.apps.bookings.entity.Booking;
import com.example.demo.apps.bookings.entity.Feature;
import com.example.demo.apps.bookings.entity.Room;
import org.jspecify.annotations.NonNull;

import java.time.LocalDateTime;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class BookingServiceImpl implements BookingService {

    private final Map<Room, Booking> bookings;
    private final Map<String, Room> rooms;

    public BookingServiceImpl(@NonNull Map<Room, Booking> bookings, @NonNull Map<String, Room> rooms) {
        this.bookings = bookings;
        this.rooms = rooms;
    }

    @Override
    public void addRoom(String name, int capacity, Set<Feature> features, int openHour, int closeHour) {
        rooms.put(name, new Room(name, capacity, features, openHour, closeHour));
    }

    @Override
    public void updateRoom(String name, int newCapacity, Set<Feature> newFeatures, int newOpenHour, int newCloseHour) {
        rooms.remove(name);
        addRoom(name, newCapacity, newFeatures, newOpenHour, newCloseHour);
    }

    @Override
    public void deleteRoom(String name) {
        rooms.remove(name);
    }

    @Override
    public Room getRoom(String roomName) {
        return rooms.get(roomName);
    }

    @Override
    public Optional<Booking> book(int start, int end, int personCount, Set<Feature> features) {
        return getAvailableRooms(start, end, personCount, features)
            .stream()
            .findFirst()
            .map(room -> {
                Booking booking = new Booking(start, end, personCount, features,
                    LocalDateTime.now(ZoneId.systemDefault()), room);
                bookings.put(room, booking);
                return booking;
            });
    }

    private List<Room> getAvailableRooms(int start, int end, int personCount, Set<Feature> features) {
        return rooms.values().stream()
            .filter(room -> room.capacity() >= personCount)
            .filter(room -> room.openHour() >= start && room.closeHour() <= end)
            .filter(room -> room.features().containsAll(features))
            .toList();
    }

    @Override
    public List<Booking> getRoomSchedule(String roomName) {
        return bookings.entrySet().stream()
            .filter(es -> es.getKey().name().equals(roomName))
            .map(Map.Entry::getValue).toList();
    }
}
