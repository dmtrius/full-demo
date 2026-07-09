package com.example.demo.apps;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class App51 {
    void main() {
        log.info("App51: Java 26 features demo");
        m1();
    }

    void m1() {
        int[] nums = {-2, 1, -3, 4, -1, 2, 1, -5, 4};
        int maxSum = 0;
        int currSum = 0;
        for (int num : nums) {
            currSum = Math.max(num, currSum + num);
            maxSum = Math.max(maxSum, currSum);
        }
        log.info("Maximum subarray sum: {}", maxSum);
    }
}
