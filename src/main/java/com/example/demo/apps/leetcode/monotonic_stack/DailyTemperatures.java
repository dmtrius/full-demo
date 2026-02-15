package com.example.demo.apps.leetcode.monotonic_stack;

import java.util.Arrays;
import java.util.Stack;

import static java.lang.IO.println;

/**
 * Monotonic Stack (<a href="https://leetcode.com/problems/daily-temperatures/">739</a>)
 * <a href="https://algo.monster/liteproblems/739">739</a>
 * <h2>Daily Temperatures</h2>
 * Given an array of integers temperatures represents the daily temperatures, return an array answer such that answer[i] is the number of days you have to wait after the ith day to get a warmer temperature. If there is no future day for which this is possible, keep answer[i] == 0 instead.
 * <p>
 * Example 1:
 * <p>
 * Input: temperatures = [73,74,75,71,69,72,76,73]
 * Output: [1,1,4,2,1,1,0,0]
 * Example 2:
 * <p>
 * Input: temperatures = [30,40,50,60]
 * Output: [1,1,1,0]
 * Example 3:
 * <p>
 * Input: temperatures = [30,60,90]
 * Output: [1,1,0]
 */
public class DailyTemperatures {
    void main() {
        int[] temperatures = {73, 74, 75, 71, 69, 72, 76, 73};
        println(Arrays.toString(dailyTemperatures(temperatures)));
    }

    int[] dailyTemperatures(int[] temperatures) {
        int n = temperatures.length;
        int[] answer = new int[n];
        Stack<Integer> stack = new Stack<>();

        for (int i = 0; i < n; i++) {
            // While current temperature is warmer than temperature at stack top
            while (!stack.isEmpty() && temperatures[i] > temperatures[stack.peek()]) {
                int prevIndex = stack.pop();
                answer[prevIndex] = i - prevIndex;
            }
            stack.push(i);
        }

        return answer;
    }
}
