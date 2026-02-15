package com.example.demo.apps.leetcode.prefix_sum;

/**
 * Prefix Sum (<a href="https://leetcode.com/problems/range-sum-query-immutable/">303</a>)
 * <a href="https://algo.monster/liteproblems/303">303</a>
 * <h2>Range Sum Query - Immutable</h2>
 * Given an integer array nums, handle multiple queries of the following type:
 * <p>
 * Calculate the sum of the elements of nums between indices left and right inclusive where left <= right.
 * Implement the NumArray class:
 * <p>
 * NumArray(int[] nums) Initializes the object with the integer array nums.
 * int sumRange(int left, int right) Returns the sum of the elements of nums between indices left and right inclusive (i.e. nums[left] + nums[left + 1] + ... + nums[right]).
 * <p>
 * <p>
 * Example 1:
 * <p>
 * Input
 * ["NumArray", "sumRange", "sumRange", "sumRange"]
 * [[[-2, 0, 3, -5, 2, -1]], [0, 2], [2, 5], [0, 5]]
 * Output
 * [null, 1, -1, -3]
 * <p>
 * Explanation
 * NumArray numArray = new NumArray([-2, 0, 3, -5, 2, -1]);
 * numArray.sumRange(0, 2); // return (-2) + 0 + 3 = 1
 * numArray.sumRange(2, 5); // return 3 + (-5) + 2 + (-1) = -1
 * numArray.sumRange(0, 5); // return (-2) + 0 + 3 + (-5) + 2 + (-1) = -3
 */
public class RangeSumQuery {
    static void main() {
        RangeSumQuery rangeSumQuery = new RangeSumQuery(new int[]{-2, 0, 3, -5, 2, -1});
        System.out.println(rangeSumQuery.sumRange(0, 2));
        RangeSumQuery obj = new RangeSumQuery(new int[]{-2, 0, 3, -5, 2, -1});
        System.out.println(obj.sumRange(0, 2));  // 1
        System.out.println(obj.sumRange(2, 5));  // -1
        System.out.println(obj.sumRange(0, 5));  // -3
    }

    private final int[] prefix;

    public RangeSumQuery(int[] nums) {
        prefix = new int[nums.length];
        if (nums.length > 0) {
            prefix[0] = nums[0];
            for (int i = 1; i < nums.length; i++) {
                prefix[i] = prefix[i - 1] + nums[i];
            }
        }
    }

    public int sumRange(int left, int right) {
        if (left == 0) {
            return prefix[right];
        }
        return prefix[right] - prefix[left - 1];
    }
}
