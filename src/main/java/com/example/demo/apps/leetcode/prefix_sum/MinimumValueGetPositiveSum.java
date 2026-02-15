package com.example.demo.apps.leetcode.prefix_sum;

import static java.lang.IO.println;

/**
 * Prefix Sum (<a href="https://leetcode.com/problems/minimum-value-to-get-positive-step-by-step-sum/">1413</a>)
 * <a href="https://algo.monster/liteproblems/1413">1413</a>
 * <h2>Minimum Value to Get Positive Step by Step Sum</h2>
 * Given an array of integers nums, you start with an initial positive value startValue.
 * <p>
 * In each iteration, you calculate the step by step sum of startValue plus elements in nums (from left to right).
 * <p>
 * Return the minimum positive value of startValue such that the step by step sum is never less than 1.
 * <p>
 * Example 1:
 * <p>
 * Input: nums = [-3,2,-3,4,2]
 * Output: 5
 * Explanation: If you choose startValue = 4, in the third iteration your step by step sum is less than 1.
 * step by step sum
 * startValue = 4 | startValue = 5 | nums
 * (4 -3 ) = 1  | (5 -3 ) = 2    |  -3
 * (1 +2 ) = 3  | (2 +2 ) = 4    |   2
 * (3 -3 ) = 0  | (4 -3 ) = 1    |  -3
 * (0 +4 ) = 4  | (1 +4 ) = 5    |   4
 * (4 +2 ) = 6  | (5 +2 ) = 7    |   2
 * Example 2:
 * <p>
 * Input: nums = [1,2]
 * Output: 1
 * Explanation: Minimum start value should be positive.
 * Example 3:
 * <p>
 * Input: nums = [1,-2,-3]
 * Output: 5
 */
public class MinimumValueGetPositiveSum {
    void main() {
        int[] nums = {-3, 2, -3, 4, 2};
        println(minStartValue(nums));
    }

    static int minStartValue(int[] nums) {
        int minSum = 0;
        int runningSum = 0;

        // Find the minimum prefix sum
        for (int num : nums) {
            runningSum += num;
            minSum = Math.min(minSum, runningSum);
        }

        // If minSum is negative, we need (1 - minSum) to make it positive
        // If minSum is 0 or positive, we need at least 1
        return 1 - minSum;
    }
}
