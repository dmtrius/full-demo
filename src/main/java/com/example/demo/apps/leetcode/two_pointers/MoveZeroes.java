package com.example.demo.apps.leetcode.two_pointers;

import java.util.Arrays;

import static java.lang.IO.println;

/**
 * Two Pointers (<a href="https://leetcode.com/problems/move-zeroes/">283</a>)
 * <a href="https://algo.monster/liteproblems/283">283</a>
 * <h2>Move Zeroes</h2>
 * Given an integer array nums, move all 0's to the end of it while maintaining the relative order of the non-zero elements.
 * <p>
 * Note that you must do this in-place without making a copy of the array.
 * <p>
 * Example 1:
 * <p>
 * Input: nums = [0,1,0,3,12]
 * Output: [1,3,12,0,0]
 * Example 2:
 * <p>
 * Input: nums = [0]
 * Output: [0]
 */
public class MoveZeroes {
    void main() {
        int[] nums = {0, 1, 0, 3, 12};
        moveZeroes(nums);
        println(Arrays.toString(nums));

    }

    void moveZeroes(int[] nums) {
        if (nums == null || nums.length == 0) {
            return;
        }

        int writeIndex = 0; // Position to write next non-zero element

        // Move all non-zero elements to the front
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != 0) {
                nums[writeIndex] = nums[i];
                writeIndex++;
            }
        }

        // Fill the rest with zeros
        while (writeIndex < nums.length) {
            nums[writeIndex] = 0;
            writeIndex++;
        }
    }

    void moveZerosSwap(int[] nums) {
        if (nums == null || nums.length == 0) {
            return;
        }

        int writeIndex = 0;

        // Swap non-zero elements to the front
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != 0) {
                // Swap only if indices are different
                if (i != writeIndex) {
                    int temp = nums[writeIndex];
                    nums[writeIndex] = nums[i];
                    nums[i] = temp;
                }
                writeIndex++;
            }
        }
    }

    // Helper method to print array
    void printArray(int[] arr) {
        System.out.print("[");
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i]);
            if (i < arr.length - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }
}
