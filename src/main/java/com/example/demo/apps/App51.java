package com.example.demo.apps;

public class App51 {
    void main() {
        IO.println("App51: Java 26 features demo");
        m1();
    }

    void m1() {
        int[] nums = {-2, 1, -3, 4, -1, 2, 1, -5, 4};
        int maxSum = 0, currSum = 0;
        for (int num : nums) {
            currSum = Math.max(num, currSum + num);
            maxSum = Math.max(maxSum, currSum);
        }
        IO.println(maxSum);
    }
}
