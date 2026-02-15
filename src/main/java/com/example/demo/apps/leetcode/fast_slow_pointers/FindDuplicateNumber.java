package com.example.demo.apps.leetcode.fast_slow_pointers;

import java.util.HashSet;
import java.util.Set;

import static java.lang.IO.println;

/**
 * Fast & Slow pointers (<a href="https://leetcode.com/problems/find-the-duplicate-number/">287</a>)
 * <a href="https://algo.monster/liteproblems/287">287</a>
 * <h2>Find theDuplicate Numbers</h2>
 * Given an array of integers nums containing n + 1 integers where each integer is in the range [1, n] inclusive.
 * <p>
 * There is only one repeated number in nums, return this repeated number.
 * <p>
 * You must solve the problem without modifying the array nums and using only constant extra space.
 * <p>
 * Example 1:
 * <p>
 * Input: nums = [1,3,4,2,2]
 * Output: 2
 * Example 2:
 * <p>
 * Input: nums = [3,1,3,4,2]
 * Output: 3
 * Example 3:
 * <p>
 * Input: nums = [3,3,3,3,3]
 * Output: 3
 */
public class FindDuplicateNumber {
    void main() {
        int[] nums = {1, 3, 4, 2, 2};
        println(findDuplicate(nums));
        println(findDuplicateBinarySearch(nums));
        println(findDuplicateHashSet(nums));
    }

    int findDuplicate(int[] nums) {
        // Phase 1: Find intersection point in the cycle
        int slow = nums[0];
        int fast = nums[0];

        do {
            slow = nums[slow];
            fast = nums[nums[fast]];
        } while (slow != fast);

        // Phase 2: Find the entrance to the cycle (duplicate number)
        slow = nums[0];
        while (slow != fast) {
            slow = nums[slow];
            fast = nums[fast];
        }

        return slow;
    }

    int findDuplicateBinarySearch(int[] nums) {
        int left = 1;
        int right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;
            int count = 0;

            // Count how many numbers are <= mid
            for (int num : nums) {
                if (num <= mid) {
                    count++;
                }
            }

            // If count > mid, duplicate is in the left half
            if (count > mid) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }

        return left;
    }

    int findDuplicateHashSet(int[] nums) {
        Set<Integer> seen = new HashSet<>();

        for (int num : nums) {
            if (seen.contains(num)) {
                return num;
            }
            seen.add(num);
        }

        return -1; // Should never reach here
    }
}
