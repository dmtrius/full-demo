package com.example.demo.apps;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.text.ListFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;

@Slf4j
public class App24 {
    @SneakyThrows
    void main() {
        log.info("hello");
        Thread vThread = Thread.ofVirtual().start(()
                -> log.info("hello from virtual thread"));
        vThread.join();

        ZoneId zoneId = ZoneId.of("America/New_York");
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
        boolean isInDST = zonedDateTime.getOffset().getTotalSeconds()
                != zoneId.getRules().getStandardOffset(zonedDateTime.toInstant()).getTotalSeconds();
        System.out.println(zoneId.getRules().getStandardOffset(zonedDateTime.toInstant()).getTotalSeconds());
        System.out.println(isInDST);

        System.out.println(10+20+"30");

        System.out.println("====");
        print();
    }

    final List<String> days = List.of("Mon", "Tue", "Wed", "Thu", "Fri");

    private void print() {
        print(ListFormat.Type.STANDARD);
        print(ListFormat.Type.OR);
        print(ListFormat.Type.UNIT);
    }

    private void print(ListFormat.Type type) {
        print(type, ListFormat.Style.FULL);
        print(type, ListFormat.Style.SHORT);
        print(type, ListFormat.Style.NARROW);
    }

    private void print(ListFormat.Type type, ListFormat.Style style) {
        var format = ListFormat.getInstance(Locale.US, type, style);
        var result = format.format(days);
        System.out.printf("%8s + %-7s: %s%n", type, style, result);
    }
}
