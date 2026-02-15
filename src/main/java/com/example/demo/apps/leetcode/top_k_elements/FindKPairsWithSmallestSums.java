package com.example.demo.apps.leetcode.top_k_elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

import static java.lang.IO.println;

/**
 * Top ‘K’ Elements (<a href="https://leetcode.com/problems/find-k-pairs-with-smallest-sums/">373</a>)
 * <a href="https://algo.monster/liteproblems/373">373</a>
 * <h2>Find K Pairs with Smallest Sums</h2>
 * You are given two integer arrays nums1 and nums2 sorted in non-decreasing order and an integer k.
 * <p>
 * Define a pair (u, v) which consists of one element from the first array and one element from the second array.
 * <p>
 * Return the k pairs (u1, v1), (u2, v2), ..., (uk, vk) with the smallest sums.
 * <p>
 * Example 1:
 * <p>
 * Input: nums1 = [1,7,11], nums2 = [2,4,6], k = 3
 * Output: [[1,2],[1,4],[1,6]]
 * Explanation: The first 3 pairs are returned from the sequence: [1,2],[1,4],[1,6],[7,2],[7,4],[11,2],[7,6],[11,4],[11,6]
 * Example 2:
 * <p>
 * Input: nums1 = [1,1,2], nums2 = [1,2,3], k = 2
 * Output: [[1,1],[1,1]]
 * Explanation: The first 2 pairs are returned from the sequence: [1,1],[1,1],[1,2],[2,1],[1,2],[2,2],[1,3],[1,3],[2,3]
 */
public class FindKPairsWithSmallestSums {
    void main() {
        int[] nums1 = {1, 7, 11}, nums2 = {2, 4, 6};
        int k = 3;
        println(kSmallestPairs(nums1, nums2, k));
        println(kSmallestPairsAlternative(nums1, nums2, k));
    }

    List<List<Integer>> kSmallestPairs(int[] nums1, int[] nums2, int k) {
        List<List<Integer>> result = new ArrayList<>();

        // Edge cases
        if (nums1 == null || nums2 == null || nums1.length == 0 || nums2.length == 0 || k == 0) {
            return result;
        }

        // Min-heap: stores [sum, index in nums1, index in nums2]
        PriorityQueue<int[]> minHeap = new PriorityQueue<>((a, b) -> a[0] - b[0]);

        // Initialize heap with pairs (nums1[i], nums2[0]) for all i
        // Only add min(k, nums1.length) elements to optimize
        for (int i = 0; i < Math.min(k, nums1.length); i++) {
            minHeap.offer(new int[]{nums1[i] + nums2[0], i, 0});
        }

        // Extract k smallest pairs
        while (k > 0 && !minHeap.isEmpty()) {
            int[] curr = minHeap.poll();
            int i = curr[1];  // index in nums1
            int j = curr[2];  // index in nums2

            result.add(Arrays.asList(nums1[i], nums2[j]));

            // If there's a next element in nums2, add the pair (nums1[i], nums2[j+1])
            if (j + 1 < nums2.length) {
                minHeap.offer(new int[]{nums1[i] + nums2[j + 1], i, j + 1});
            }

            k--;
        }

        return result;
    }

    public List<List<Integer>> kSmallestPairsAlternative(int[] nums1, int[] nums2, int k) {
        List<List<Integer>> result = new ArrayList<>();

        if (nums1.length == 0 || nums2.length == 0 || k == 0) {
            return result;
        }

        // Min-heap based on sum
        PriorityQueue<int[]> minHeap = new PriorityQueue<>(
                (a, b) -> (nums1[a[0]] + nums2[a[1]]) - (nums1[b[0]] + nums2[b[1]])
        );

        // Start with all pairs from first row
        for (int j = 0; j < Math.min(nums2.length, k); j++) {
            minHeap.offer(new int[]{0, j});
        }

        while (k > 0 && !minHeap.isEmpty()) {
            int[] idx = minHeap.poll();
            result.add(Arrays.asList(nums1[idx[0]], nums2[idx[1]]));

            if (idx[0] + 1 < nums1.length) {
                minHeap.offer(new int[]{idx[0] + 1, idx[1]});
            }
            k--;
        }

        return result;
    }
}
