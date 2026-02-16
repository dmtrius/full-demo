package com.example.demo.apps;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class App39Test {
    private final App39 app = new App39();
    @Test
    void test() {
        assertEquals(1, app.t1());
    }
}
