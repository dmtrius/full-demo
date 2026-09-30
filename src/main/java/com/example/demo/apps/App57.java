package com.example.demo.apps;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class App57 {
    void main() {
        IO.println("Hello World");
        byte[] hash128 = Hashing.murmur3_128(Integer.MAX_VALUE)
            .hashString("Hello World", StandardCharsets.UTF_8).asBytes();
        IO.println(Arrays.toString(hash128));
    }
}
