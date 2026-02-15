package com.example.demo.apps.leetcode.dynamic_programming;

import java.util.Arrays;

import static java.lang.IO.println;

/**
 * Dynamic Programming (<a href="https://leetcode.com/problems/longest-increasing-subsequence/">300</a>)
 * <a href="https://algo.monster/liteproblems/300">300</a>
 * <h2>Longest Increasing Subsequence</h2>
 * Given an integer array nums, return the length of the longest strictly increasing subsequence.
 * <p>
 * Example 1:
 * <p>
 * Input: nums = [10,9,2,5,3,7,101,18]
 * Output: 4
 * Explanation: The longest increasing subsequence is [2,3,7,101], therefore the length is 4.
 * Example 2:
 * <p>
 * Input: nums = [0,1,0,3,2,3]
 * Output: 4
 * Example 3:
 * <p>
 * Input: nums = [7,7,7,7,7,7,7]
 * Output: 1
 */
public class LongestIncreasingSubsequence {
    void main() {
        int[] nums = {10, 9, 2, 5, 3, 7, 101, 18};
        println(lengthOfLIS(nums));
        println(lengthOfLISOptimized(nums));
    }

    int lengthOfLIS(int[] nums) {
        int n = nums.length;
        int[] dp = new int[n];
        Arrays.fill(dp, 1);
        int maxLen = 1;

        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (nums[i] > nums[j]) {
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }
            maxLen = Math.max(maxLen, dp[i]);
        }
        return maxLen;
    }

    int lengthOfLISOptimized(int[] nums) {
        int[] tails = new int[nums.length];
        int len = 0;
        for (int num : nums) {
            int i = Arrays.binarySearch(tails, 0, len, num);
            if (i < 0) {
                i = -(i + 1);
            }
            tails[i] = num;
            if (i == len) {
                len++;
            }
        }
        return len;
    }

}
