package com.example.demo.apps;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.IntStream;

@Slf4j
public class App3 {
    public static void main(String... args) {
        final int[] ints = testInts(12);
        ArrayUtils.shuffle(ints);
        sortPrimitives(ints);
    }

    private static void sortPrimitives(int[] primitives) {
        log.info("Before sorting: " + Arrays.toString(primitives));
        primitives = IntStream.of(primitives).boxed().sorted(Collections.reverseOrder()).mapToInt(Integer::intValue).toArray();
        log.info("After sorting: " + Arrays.toString(primitives));
    }

    private static int[] testInts(int size) {
        return IntStream.range(0,10).limit(size).unordered().toArray();
    }
}
