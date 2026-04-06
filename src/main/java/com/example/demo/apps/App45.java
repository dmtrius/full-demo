package com.example.demo.apps;

import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.uuid.UuidCreator;

import java.util.UUID;

import static java.lang.IO.println;

public class App45 {
    void main() {
        println("Hey");
        m1();
    }

    void m1() {
        Ulid ulid = Ulid.fast();
        String id = ulid.toString();
        println("ULID" + id);

        UUID v4 = UUID.randomUUID();
        println(v4);
        println("Variant: " + v4.variant());
        println("Version: " + v4.version());

        UUID v7 = UuidCreator.getTimeOrderedEpoch();
        println(v7);
        println("Variant: " + v7.variant());
        println("Version: " + v7.version());
    }
}
