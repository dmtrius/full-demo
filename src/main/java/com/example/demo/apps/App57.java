package com.example.demo.apps;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class App57 {
    @SuppressWarnings("UnstableApiUsage")
    void main() {
        var str = "Hello World";
        IO.println(str);
        byte[] hash128 = Hashing.murmur3_128(Integer.MAX_VALUE)
            .hashString(str, StandardCharsets.UTF_8).asBytes();
        IO.println(Arrays.toString(hash128));
    }
}
