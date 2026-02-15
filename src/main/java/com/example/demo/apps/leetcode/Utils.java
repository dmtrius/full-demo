package com.example.demo.apps.leetcode;

public class Utils {
    public static ListNode createList(int[] values) {
        if (values == null || values.length == 0) {
            return null;
        }

        ListNode head = new ListNode(values[0]);
        ListNode current = head;

        for (int i = 1; i < values.length; i++) {
            current.next = new ListNode(values[i]);
            current = current.next;
        }

        return head;
    }

    // Helper method to create a cycle by connecting tail to a specific position
    public static void createCycle(ListNode head, int pos) {
        if (head == null || pos < 0) {
            return;
        }

        ListNode tail = head;
        ListNode cycleNode = null;
        int index = 0;

        // Find the node at position 'pos' and the tail
        while (tail.next != null) {
            if (index == pos) {
                cycleNode = tail;
            }
            tail = tail.next;
            index++;
        }

        // Check if pos is the last node
        if (index == pos) {
            cycleNode = tail;
        }

        // Create the cycle
        if (cycleNode != null) {
            tail.next = cycleNode;
        }
    }
}
