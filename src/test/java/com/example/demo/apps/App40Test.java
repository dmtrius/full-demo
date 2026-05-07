package com.example.demo.apps;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class App40Test {
    private final App40 app = new App40();

    @Test
    void test_m1() {
        assertEquals(5, app.m1("Hello"));
    }
}
