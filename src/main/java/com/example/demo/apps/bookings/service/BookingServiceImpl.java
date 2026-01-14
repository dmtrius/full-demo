package com.example.demo.apps.bookings.service;

import com.example.demo.apps.bookings.entity.Booking;
import com.example.demo.apps.bookings.entity.Feature;
import com.example.demo.apps.bookings.entity.Room;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class BookingServiceImpl implements BookingService {

    private final Map<Room, Booking> bookings;
    private final Map<String, Room> rooms;

    public BookingServiceImpl(Map<Room, Booking> bookings, Map<String, Room> rooms) {
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
    public Booking book(int start, int end, int personCount, Set<Feature> features) {
        var availableRooms = getAvailableRooms(start, end, personCount, features);
        if (!availableRooms.isEmpty()) {
            Room room = availableRooms.getFirst();
            Booking booking = new Booking(start, end, personCount, features, room);
            bookings.put(room, booking);
            return booking;
        }
        return null;
    }

    private List<Room> getAvailableRooms(int start, int end, int personCount, Set<Feature> features) {
        return rooms.values().stream()
            .filter(room -> room.capacity() >= personCount)
            .filter(room -> room.openHour() <= start
                && room.closeHour() >= end)
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
