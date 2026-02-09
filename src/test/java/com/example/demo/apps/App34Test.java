package com.example.demo.apps;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class App34Test {

    @Test
    void test_valid() {
        String input = "aabbccdhdeeeff";
        assertEquals('h', App34.find1stNunDuplicate(input).get());
    }

    @Test
    void test_invalid() {
        String input = "aabbccddeeeff";
        assertTrue(App34.find1stNunDuplicate(input).isEmpty());
    }

    @Test
    void test_null() {
        assertThrows(NullPointerException.class ,() -> App34.find1stNunDuplicate(null));
    }

    @Test
    void test_empty() {
        assertTrue(App34.find1stNunDuplicate("").isEmpty());
    }
}
