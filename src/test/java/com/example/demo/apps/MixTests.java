package com.example.demo.apps;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MixTests {
    @Test
    void test1() {
        try (MockedStatic<C1> mocked = mockStatic(C1.class)) {
            mocked.when(() -> C1.m1(anyString())).thenAnswer(invocation ->
                "CALL: " + invocation.getArgument(0));
            assertEquals("HELLO", C1.m1("hello"));
            mocked.verify(() -> C1.m1(anyString()), times(1));
        }
    }
}

class C1 {
    public static String m1(String str) {
        return str.toUpperCase();
    }
}
