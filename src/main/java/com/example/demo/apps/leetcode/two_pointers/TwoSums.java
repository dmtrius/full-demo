package com.example.demo.apps.leetcode.two_pointers;

import java.util.Arrays;

import static java.lang.IO.println;

/**
 * Two Pointers (<a href="https://leetcode.com/problems/two-sum-ii-input-array-is-sorted/">167</a>)
 * <a href="https://algo.monster/liteproblems/167">167</a>
 * <h2>Two Sum II - Input Array Is Sorted</h2>
 * Given a 1-indexed array of integers numbers that is already sorted in non-decreasing order, find two numbers such that they add up to a specific target number. Let these two numbers be numbers[index1] and numbers[index2] where 1 <= index1 < index2 <= numbers.length.
 * <p>
 * Return the indices of the two numbers, index1 and index2, added by one as an integer array [index1, index2] of length 2.
 * <p>
 * The tests are generated such that there is exactly one solution. You may not use the same element twice.
 * <p>
 * Your solution must use only constant extra space.
 * <p>
 * Example 1:
 * <p>
 * Input: numbers = [2,7,11,15], target = 9
 * Output: [1,2]
 * Explanation: The sum of 2 and 7 is 9. Therefore, index1 = 1, index2 = 2. We return [1, 2].
 * Example 2:
 * <p>
 * Input: numbers = [2,3,4], target = 6
 * Output: [1,3]
 * Explanation: The sum of 2 and 4 is 6. Therefore index1 = 1, index2 = 3. We return [1, 3].
 * Example 3:
 * <p>
 * Input: numbers = [-1,0], target = -1
 * Output: [1,2]
 * Explanation: The sum of -1 and 0 is -1. Therefore index1 = 1, index2 = 2. We return [1, 2].
 */
public class TwoSums {
    void main() {
        int[] n = twoSum(new int[]{2, 7, 11, 15}, 9);
        println(Arrays.toString(n));
    }

    static int[] twoSum(int[] numbers, int target) {
        int left = 0;
        int right = numbers.length - 1;

        while (left < right) {
            int currentSum = numbers[left] + numbers[right];

            if (currentSum == target) {
                // Return 1-indexed positions
                return new int[]{left + 1, right + 1};
            } else if (currentSum < target) {
                // Need a larger sum, move left pointer right
                left++;
            } else {
                // Need a smaller sum, move right pointer left
                right--;
            }
        }

        // Should never reach here given problem constraints
        return new int[]{};
    }
}
