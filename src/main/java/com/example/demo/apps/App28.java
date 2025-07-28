package com.example.demo.apps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.vavr.API.println;

public class App28 {
    void main() {
        int[] example = {-1, -2, -3, 2};
        System.out.println(processArray(example));
    }

    public static List<Integer> processArray(int[] inputNumbers) {
        List<Integer> result = new ArrayList<>();
        Arrays.stream(inputNumbers).forEach(v -> {
            if (v < 0) {
                result.add(v);
            } else {
                if (v < result.size()) {
                    result.remove(v);
                }
            }
        });
        return result;
    }
}
