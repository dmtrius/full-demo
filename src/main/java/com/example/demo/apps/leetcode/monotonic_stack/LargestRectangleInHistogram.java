package com.example.demo.apps.leetcode.monotonic_stack;

import java.util.Stack;

import static java.lang.IO.println;

/**
 * Monotonic Stack (<a href="https://leetcode.com/problems/largest-rectangle-in-histogram/">84</a>)
 * <a href="https://algo.monster/liteproblems/84">84</a>
 * <h2>Largest Rectangle in Histogram</h2>
 * Given an array of integers heights representing the histogram's bar height where the width of each bar is 1, return the area of the largest rectangle in the histogram.
 * <p>
 * Example 1:
 * <p>
 * Input: heights = [2,1,5,6,2,3]
 * Output: 10
 * Explanation: The above is a histogram where width of each bar is 1.
 * The largest rectangle is shown in the red area, which has an area = 10 units.
 * Example 2:
 * <p>
 * <p>
 * Input: heights = [2,4]
 * Output: 4
 */
public class LargestRectangleInHistogram {
    void main() {
        int[] heights = {2, 1, 5, 6, 2, 3};
        println(largestRectangleArea(heights));
        println(largestRectangleAreaSentinel(heights));
    }

    int largestRectangleArea(int[] heights) {
        Stack<Integer> stack = new Stack<>();
        int maxArea = 0;
        int n = heights.length;

        for (int i = 0; i < n; i++) {
            // While current bar is shorter than stack top, calculate area
            while (!stack.isEmpty() && heights[i] < heights[stack.peek()]) {
                int height = heights[stack.pop()];
                int width = stack.isEmpty() ? i : i - stack.peek() - 1;
                maxArea = Math.max(maxArea, height * width);
            }
            stack.push(i);
        }

        // Process remaining bars in stack
        while (!stack.isEmpty()) {
            int height = heights[stack.pop()];
            int width = stack.isEmpty() ? n : n - stack.peek() - 1;
            maxArea = Math.max(maxArea, height * width);
        }

        return maxArea;
    }

    int largestRectangleAreaSentinel(int[] heights) {
        Stack<Integer> stack = new Stack<>();
        stack.push(-1); // Sentinel for width calculation
        int maxArea = 0;

        for (int i = 0; i < heights.length; i++) {
            while (stack.peek() != -1 && heights[i] < heights[stack.peek()]) {
                int height = heights[stack.pop()];
                int width = i - stack.peek() - 1;
                maxArea = Math.max(maxArea, height * width);
            }
            stack.push(i);
        }

        while (stack.peek() != -1) {
            int height = heights[stack.pop()];
            int width = heights.length - stack.peek() - 1;
            maxArea = Math.max(maxArea, height * width);
        }

        return maxArea;
    }
}
