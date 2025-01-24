package com.example.demo.apps;

import lombok.SneakyThrows;
import org.apache.commons.text.StringSubstitutor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class App12 {
    @SneakyThrows
    public static void main(String... args) {
        String baseString = "${third}: String ${first} in ${second} with some ${second} examples.";
        String first = "Interpolation";
        String second = "Java";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("first", first);
        parameters.put("second", second);
        parameters.put("third", 42);
        StringSubstitutor substitutor = new StringSubstitutor(parameters);
        String result = substitutor.replace(baseString);
        System.out.println(result);

        int[] nums = new int[]{0, 0, 0, 1, 0, 5, 6, 0, 45, 66, 0, 0};
        int lastNZ = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != 0) {
                int temp = nums[lastNZ];
                nums[lastNZ] = nums[i];
                nums[i] = temp;
                ++lastNZ;
            }
        }
        System.out.println(">>> " + Arrays.toString(nums));

        // test JProfiler
//        TimeUnit.MINUTES.sleep(20);
    }
}
/*
001
010

 */