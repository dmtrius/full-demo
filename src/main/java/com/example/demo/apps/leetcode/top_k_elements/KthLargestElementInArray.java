package com.example.demo.apps.leetcode.top_k_elements;

import java.util.PriorityQueue;

import static java.lang.IO.println;

/**
 * Top ‘K’ Elements (<a href="https://leetcode.com/problems/kth-largest-element-in-an-array/">215</a>)
 * <a href="https://algo.monster/liteproblems/215">215</a>
 * <h2>K-th Largest Element in an Array</h2>
 * Given an integer array nums and an integer k, return the kth largest element in the array.
 * <p>
 * Note that it is the kth largest element in the sorted order, not the kth distinct element.
 * <p>
 * Can you solve it without sorting?
 * <p>
 * Example 1:
 * <p>
 * Input: nums = [3,2,1,5,6,4], k = 2
 * Output: 5
 * Example 2:
 * <p>
 * Input: nums = [3,2,3,1,2,4,5,5,6], k = 4
 * Output: 4
 */
public class KthLargestElementInArray {
    void main() {
        int[] nums = {3, 2, 3, 1, 2, 4, 5, 5, 6};
        int k = 4;
        int result = findKthLargest(nums, k);
        println(result);
    }

    int findKthLargest(int[] nums, int k) {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();

        for (int num : nums) {
            minHeap.offer(num);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }

        return minHeap.peek();
    }

}
