package com.example.demo.apps.leetcode.linkedlist_reversal;

import com.example.demo.apps.leetcode.ListNode;

import static com.example.demo.apps.leetcode.Utils.createList;
import static java.lang.IO.println;

/**
 * LinkedList In-Place Reversal (<a href="https://leetcode.com/problems/swap-nodes-in-pairs/">24</a>)
 * <a href="https://algo.monster/liteproblems/24">24</a>
 * <h2>Swap Nodes in Pairs</h2>
 * Given a linked list, swap every two adjacent nodes and return its head. You must solve the problem without modifying the values in the list's nodes (i.e., only nodes themselves may be changed.)
 * <p>
 * Example 1:
 * <p>
 * Input: head = [1,2,3,4]
 * <p>
 * Output: [2,1,4,3]
 * <p>
 * Explanation:
 * <p>
 * Example 2:
 * <p>
 * Input: head = []
 * <p>
 * Output: []
 * <p>
 * Example 3:
 * <p>
 * Input: head = [1]
 * <p>
 * Output: [1]
 * <p>
 * Example 4:
 * <p>
 * Input: head = [1,2,3]
 * <p>
 * Output: [2,1,3]
 */
public class SwapNodesInPairs {
    void main() {
        int[] nums = {1, 2, 3, 4};
        var head = createList(nums);
        println(head);
        println(swapPairs(head));
        head = createList(nums);
        println(swapPairsRecursive(head));
    }

    ListNode swapPairs(ListNode head) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode prev = dummy;

        while (head != null && head.next != null) {
            ListNode first = head;
            ListNode second = head.next;

            // Swap: prev -> second -> first -> rest
            prev.next = second;
            first.next = second.next;
            second.next = first;

            // Move prev to first (end of swapped pair)
            prev = first;
            head = first.next;
        }

        return dummy.next;
    }

    ListNode swapPairsRecursive(ListNode head) {
        if (head == null || head.next == null) return head;

        ListNode second = head.next;
        head.next = swapPairsRecursive(second.next);
        second.next = head;

        return second;
    }
}
