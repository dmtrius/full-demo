package com.example.demo.apps.leetcode.modified_binary_search;

import static java.lang.IO.println;

/**
 * Modified Binary Search (<a href="https://leetcode.com/problems/find-peak-element/">162</a>)
 * <a href="https://algo.monster/liteproblems/162">162</a>
 *
 * <h2>Find Peak Element</h2>
 * A peak element is an element that is strictly greater than its neighbors.
 * <p>
 * Given a 0-indexed integer array nums, find a peak element, and return its index. If the array contains multiple peaks, return the index to any of the peaks.
 * <p>
 * You may imagine that nums[-1] = nums[n] = -∞. In other words, an element is always considered to be strictly greater than a neighbor that is outside the array.
 * <p>
 * You must write an algorithm that runs in O(log n) time.
 * <p>
 * Example 1:
 * <p>
 * Input: nums = [1,2,3,1]
 * Output: 2
 * Explanation: 3 is a peak element and your function should return the index number 2.
 * Example 2:
 * <p>
 * Input: nums = [1,2,1,3,5,6,4]
 * Output: 5
 * Explanation: Your function can return either index number 1 where the peak element is 2, or index number 5 where the peak element is 6.
 */
public class FindPeakElement {
    void main() {
        int[] nums = {1, 2, 1, 3, 5, 6, 4};
        println(findPeakElement(nums));
    }

    int findPeakElement(int[] nums) {
        int left = 0;
        int right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            // Peak on left half (including mid)
            if (nums[mid] > nums[mid + 1]) {
                right = mid;
            } else {
                // Peak on right half
                left = mid + 1;
            }
        }

        return left;
    }
}
