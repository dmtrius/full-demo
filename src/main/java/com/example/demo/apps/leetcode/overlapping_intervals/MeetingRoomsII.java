package com.example.demo.apps.leetcode.overlapping_intervals;

import java.util.Arrays;
import java.util.PriorityQueue;

import static java.lang.IO.println;

/**
 * Overlapping Intervals (<a href="https://leetcode.com/problems/meeting-rooms-ii/">253</a>)
 * <a href="https://algo.monster/liteproblems/253">253</a>
 *
 * <h2>Meeting Rooms II</h2>
 * Problem
 * Given an array of meeting time intervals intervals where intervals[i] = [start_i, end_i], return the minimum number of conference rooms required.
 * Solution Approach
 * The key insight is to track when rooms become occupied and when they become free. We can use a min-heap (priority queue) to track the end times of ongoing meetings.
 * Algorithm:
 * <p>
 * Sort meetings by start time
 * Use a min-heap to track end times of ongoing meetings
 * For each meeting:
 * <p>
 * Remove all meetings that have ended (end time ≤ current start time)
 * Add current meeting's end time to heap
 * The heap size represents rooms needed at this point
 * <p>
 * <p>
 * Return the maximum heap size seen
 */
public class MeetingRoomsII {
    void main() {
        int[][] rooms = {{0, 30}, {5, 10}, {15, 20}};
        println(minMeetingRooms(rooms));
        println(minMeetingRoomsChronologicalOrder(rooms));
    }

    int minMeetingRooms(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return 0;
        }

        // Sort meetings by start time
        Arrays.sort(intervals, (a, b) -> a[0] - b[0]);

        // Min-heap to track end times of ongoing meetings
        PriorityQueue<Integer> heap = new PriorityQueue<>();

        // Add first meeting's end time
        heap.offer(intervals[0][1]);

        // Process remaining meetings
        for (int i = 1; i < intervals.length; i++) {
            // If earliest ending meeting has ended, remove it (room becomes free)
            if (intervals[i][0] >= heap.peek()) {
                heap.poll();
            }

            // Add current meeting's end time
            heap.offer(intervals[i][1]);
        }

        // Heap size = number of rooms needed
        return heap.size();
    }

    int minMeetingRoomsChronologicalOrder(int[][] intervals) {
        int n = intervals.length;
        int[] starts = new int[n];
        int[] ends = new int[n];

        // Separate start and end times
        for (int i = 0; i < n; i++) {
            starts[i] = intervals[i][0];
            ends[i] = intervals[i][1];
        }

        // Sort both arrays
        Arrays.sort(starts);
        Arrays.sort(ends);

        int rooms = 0;
        int maxRooms = 0;
        int endPtr = 0;

        // Process each start time
        for (int start : starts) {
            // Free up rooms for meetings that have ended
            while (endPtr < n && ends[endPtr] <= start) {
                rooms--;
                endPtr++;
            }

            // Allocate a room for current meeting
            rooms++;
            maxRooms = Math.max(maxRooms, rooms);
        }

        return maxRooms;
    }
}
