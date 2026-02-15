package com.example.demo.apps.leetcode.overlapping_intervals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.IO.println;

/**
 * Overlapping Intervals (<a href="https://leetcode.com/problems/merge-intervals/description/">56</a>)
 * <a href="https://algo.monster/liteproblems/56">56</a>
 *
 * <h2>Merge Intervals</h2>
 * Given an array of intervals where intervals[i] = [starti, endi], merge all overlapping intervals, and return an array of the non-overlapping intervals that cover all the intervals in the input.
 * <p>
 * Example 1:
 * <p>
 * Input: intervals = [[1,3],[2,6],[8,10],[15,18]]
 * Output: [[1,6],[8,10],[15,18]]
 * Explanation: Since intervals [1,3] and [2,6] overlap, merge them into [1,6].
 * Example 2:
 * <p>
 * Input: intervals = [[1,4],[4,5]]
 * Output: [[1,5]]
 * Explanation: Intervals [1,4] and [4,5] are considered overlapping.
 * Example 3:
 * <p>
 * Input: intervals = [[4,7],[1,4]]
 * Output: [[1,7]]
 * Explanation: Intervals [1,4] and [4,7] are considered overlapping.
 */
public class MergeIntervals {
    void main() {
        int[][] intervals = {{1, 3}, {2, 6}, {8, 10}, {15, 18}};
        println(Arrays.deepToString(merge(intervals)));
        println(Arrays.deepToString(mergeAlternative(intervals)));
    }

    int[][] merge(int[][] intervals) {
        // Edge case: empty or single interval
        if (intervals == null || intervals.length <= 1) {
            return intervals;
        }

        // Sort intervals by start time
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        // Use a list to store merged intervals
        List<int[]> merged = new ArrayList<>();

        // Add first interval to start
        int[] currentInterval = intervals[0];
        merged.add(currentInterval);

        // Iterate through remaining intervals
        for (int i = 1; i < intervals.length; i++) {
            int currentEnd = currentInterval[1];
            int nextStart = intervals[i][0];
            int nextEnd = intervals[i][1];

            // Check if intervals overlap
            if (nextStart <= currentEnd) {
                // Merge by updating the end time
                currentInterval[1] = Math.max(currentEnd, nextEnd);
            } else {
                // No overlap, add new interval
                currentInterval = intervals[i];
                merged.add(currentInterval);
            }
        }

        // Convert list to array
        return merged.toArray(new int[merged.size()][]);
    }

    int[][] mergeAlternative(int[][] intervals) {
        if (intervals.length <= 1) return intervals;

        Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
        List<int[]> result = new ArrayList<>();

        int[] prev = intervals[0];

        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] <= prev[1]) {
                prev[1] = Math.max(prev[1], intervals[i][1]);
            } else {
                result.add(prev);
                prev = intervals[i];
            }
        }
        result.add(prev); // Don't forget the last interval

        return result.toArray(new int[result.size()][]);
    }
}
