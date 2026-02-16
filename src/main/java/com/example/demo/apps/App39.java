package com.example.demo.apps;

import java.util.ArrayList;
import java.util.List;

import static java.lang.IO.println;

public class App39 {
    void main() {
        int[] input = {-1, -2, 2};
        List<Integer> output = processArray(input);
        println(output);
    }

    public int t1() {
        return 1;
    }
    public List<Integer> processArray(int[] input) {
        boolean[] skip = new boolean[input.length];
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < input.length; i++) {
            if (input[i] > 0) {
                skip[i] = true;
            }
        }
        for (int i = 0; i < input.length; i++) {
            if (input[i] < 0 && !skip[i]) {
                result.add(input[i]);
            }
        }
        return result;
    }
}
