package com.example.demo.apps;

import com.google.common.hash.Hashing;
import lombok.extern.log4j.Log4j2;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Log4j2
public class App57 {
    @SuppressWarnings("UnstableApiUsage")
    void main() {
        var str = "Hello World";
        log.info(str);
        byte[] hash128 = Hashing.murmur3_128(Integer.MAX_VALUE)
            .hashString(str, StandardCharsets.UTF_8).asBytes();
        log.info(Arrays.toString(hash128));
    }
}
