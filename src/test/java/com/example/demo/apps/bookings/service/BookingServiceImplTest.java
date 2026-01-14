package com.example.demo.apps.bookings.service;

import com.example.demo.apps.bookings.entity.Booking;
import com.example.demo.apps.bookings.entity.Feature;
import com.example.demo.apps.bookings.entity.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.example.demo.apps.bookings.entity.Feature.PROJECTOR;
import static com.example.demo.apps.bookings.entity.Feature.VIDEO_CONFERENCE;
import static com.example.demo.apps.bookings.entity.Feature.WHITEBOARD;
import static org.junit.jupiter.api.Assertions.*;

class BookingServiceImplTest {
    private BookingServiceImpl service;
    Map<String, Room> rooms = new HashMap<>();
    private final Map<Room, Booking> bookings = new HashMap<>();

    @BeforeEach
    public void init() {
        Set<Feature> f1 = Set.of(
                PROJECTOR,
                WHITEBOARD
        );
        Set<Feature> f2 = Set.of(
                PROJECTOR,
                VIDEO_CONFERENCE
        );
        Room r1 = new Room("R1", 5, f1, 9, 18);
        Room r2 = new Room("R2", 5, f2, 12, 16);
        rooms.put(r1.name(), r1);
        rooms.put(r2.name(), r2);
        Booking b1 = new Booking(12, 13, 8, f1, r1);
        Booking b2 = new Booking(8, 14, 12, f2, r2);
        bookings.put(r1, b1);
        bookings.put(r2, b2);
        service = new BookingServiceImpl(bookings, rooms);
    }

    @Test
    void addRoom() {
        Set<Feature> f1 = Set.of(
                PROJECTOR,
                WHITEBOARD
        );
        service.addRoom("R1", 5, f1, 9, 18);
        assertTrue(rooms.containsKey("R1"));
    }

    @Test
    void updateRoom() {
        Room r = service.getRoom("R1");
        service.updateRoom(r.name(), 3, Set.of(WHITEBOARD), 8, 20);
        Room updated = service.getRoom("R1");
        assertEquals("R1", updated.name());
        assertEquals(3, updated.capacity());
        assertEquals(8, updated.openHour());
        assertEquals(20, updated.closeHour());
        assertEquals(Set.of(WHITEBOARD), updated.features());
    }

    @Test
    void deleteRoom() {
        Set<Feature> f1 = Set.of(
                PROJECTOR,
                WHITEBOARD
        );
        service.addRoom("R1", 5, f1, 9, 18);
        assertTrue(rooms.containsKey("R1"));
        service.deleteRoom("R1");
        assertFalse(rooms.containsKey("R1"));
    }

    @Test
    void book() {
        var booking = service.book(11, 14, 3, Set.of(PROJECTOR));
        assertNotNull(booking);
        assertEquals("R1", booking.room().name());
    }

    @Test
    void getRoomSchedule() {
        var res = service.getRoomSchedule("R1");
        assertEquals(1, res.size());
    }
}