package com.example.demo.apps.leetcode.backtracking;

import java.util.ArrayList;
import java.util.List;

import static java.lang.IO.println;

/**
 * Backtracking (<a href="https://leetcode.com/problems/subsets/">78</a>)
 * <a href="https://algo.monster/liteproblems/78">78</a>
 * <h2>Subset</h2>
 * Given an integer array nums of unique elements, return all possible subsets (the power set).
 * <p>
 * The solution set must not contain duplicate subsets. Return the solution in any order.
 * <p>
 * Example 1:
 * <p>
 * Input: nums = [1,2,3]
 * Output: [[],[1],[2],[1,2],[3],[1,3],[2,3],[1,2,3]]
 * Example 2:
 * <p>
 * Input: nums = [0]
 * Output: [[],[0]]
 */
public class Subset {
    void main() {
        int[] nums = {1, 2, 3};
        println(subsets(nums));
    }

    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        generateSubsets(new ArrayList<>(), 0, nums, result);
        return result;
    }

    void generateSubsets(List<Integer> subset, int index, int[] nums, List<List<Integer>> result) {
        // Always add current subset (handles exclude path)
        result.add(new ArrayList<>(subset));

        // Try including from index onward
        for (int i = index; i < nums.length; i++) {
            subset.add(nums[i]);
            generateSubsets(subset, i + 1, nums, result);
            subset.removeLast();  // Backtrack
        }
    }
}
