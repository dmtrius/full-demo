package com.example.demo.apps.leetcode.prefix_sum;

import java.util.HashMap;
import java.util.Map;

import static java.lang.IO.println;

/**
 * Prefix Sum (<a href="https://leetcode.com/problems/subarray-sum-equals-k/">560</a>)
 * <a href="https://algo.monster/liteproblems/560">560</a>
 * <h2>Subarray Sum Equals K</h2>
 * Given an array of integers nums and an integer k, return the total number of subarrays whose sum equals to k.
 * <p>
 * A subarray is a contiguous non-empty sequence of elements within an array.
 * <p>
 * Example 1:
 * <p>
 * Input: nums = [1,1,1], k = 2
 * Output: 2
 * Example 2:
 * <p>
 * Input: nums = [1,2,3], k = 3
 * Output: 2
 */
public class SubarraySumEqualsK {
    void main() {
        println(subarraySum(new int[]{1, 1, 1}, 2));
    }

    public int subarraySum(int[] nums, int k) {
        Map<Integer, Integer> prefixSumFreq = new HashMap<>();
        prefixSumFreq.put(0, 1);  // Handle subarrays starting from index 0
        int count = 0;
        int prefixSum = 0;

        for (int num : nums) {
            prefixSum += num;
            // Check if (prefixSum - k) exists in map
            count += prefixSumFreq.getOrDefault(prefixSum - k, 0);
            // Update current prefix sum frequency
            prefixSumFreq.put(prefixSum, prefixSumFreq.getOrDefault(prefixSum, 0) + 1);
        }

        return count;
    }
}
