package com.example.demo.apps.leetcode.top_k_elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;

import static java.lang.IO.println;

/**
 * Top ‘K’ Elements (<a href="https://leetcode.com/problems/top-k-frequent-elements/">347</a>)
 * <a href="https://algo.monster/liteproblems/347">347</a>
 * <h2>Top K Frequent Elements</h2>
 * Given an integer array nums and an integer k, return the k most frequent elements. You may return the answer in any order.
 * <p>
 * Example 1:
 * <p>
 * Input: nums = [1,1,1,2,2,3], k = 2
 * <p>
 * Output: [1,2]
 * <p>
 * Example 2:
 * <p>
 * Input: nums = [1], k = 1
 * <p>
 * Output: [1]
 * <p>
 * Example 3:
 * <p>
 * Input: nums = [1,2,1,2,1,2,3,1,3,2], k = 2
 * <p>
 * Output: [1,2]
 */
public class TopKFrequentElements {
    void main() {
        int[] nums = {1, 1, 1, 2, 2, 3};
        int k = 2;
        println(Arrays.toString(topKFrequent(nums, k)));
        println(Arrays.toString(topKFrequentBucketSort(nums, k)));
    }

    public int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int n : nums) {
            freq.put(n, freq.getOrDefault(n, 0) + 1);
        }

        PriorityQueue<Integer> minHeap = new PriorityQueue<>((a, b) -> freq.get(a) - freq.get(b));
        for (int num : freq.keySet()) {
            minHeap.offer(num);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }

        int[] result = new int[k];
        for (int i = k - 1; i >= 0; i--) {
            if (Objects.nonNull(minHeap.peek())) {
                result[i] = minHeap.poll();
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public int[] topKFrequentBucketSort(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int n : nums) {
            freq.put(n, freq.getOrDefault(n, 0) + 1);
        }

        List<Integer>[] buckets = new List[nums.length + 1];
        for (int num : freq.keySet()) {
            int f = freq.get(num);
            if (buckets[f] == null) {
                buckets[f] = new ArrayList<>();
            }
            buckets[f].add(num);
        }

        List<Integer> result = new ArrayList<>();
        for (int i = buckets.length - 1; i >= 0 && result.size() < k; i--) {
            if (buckets[i] != null) {
                result.addAll(buckets[i]);
            }
        }
        return result.stream().mapToInt(Integer::intValue).toArray();
    }
}
