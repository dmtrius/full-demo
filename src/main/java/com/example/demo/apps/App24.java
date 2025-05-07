package com.example.demo.apps;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;

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
    }
}
