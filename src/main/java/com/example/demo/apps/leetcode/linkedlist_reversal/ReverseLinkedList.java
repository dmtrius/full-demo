package com.example.demo.apps.leetcode.linkedlist_reversal;

import com.example.demo.apps.leetcode.ListNode;

import static com.example.demo.apps.leetcode.Utils.createList;
import static java.lang.IO.println;

/**
 * LinkedList In-Place Reversal (<a href="https://leetcode.com/problems/reverse-linked-list/">206</a>)
 * <a href="https://algo.monster/liteproblems/206">206</a>
 * <h2>Reverse Linked List</h2>
 * Given the head of a singly linked list, reverse the list, and return the reversed list.
 * <p>
 * Example 1:
 * <p>
 * Input: head = [1,2,3,4,5]
 * Output: [5,4,3,2,1]
 * Example 2:
 * <p>
 * Input: head = [1,2]
 * Output: [2,1]
 * Example 3:
 * <p>
 * Input: head = []
 * Output: []
 */
public class ReverseLinkedList {
    void main() {
        int[] nums = {3, 2, 0, -4, 10};
        var head = createList(nums);
        println(head);
        ListNode reversed = reverseList(head);
        println(reversed);
        reversed = reverseListRecursive(reversed);
        println(reversed);
    }

    ListNode reverseList(ListNode head) {
        ListNode prev = null;
        ListNode curr = head;

        while (curr != null) {
            ListNode nextTemp = curr.next;
            curr.next = prev;
            prev = curr;
            curr = nextTemp;
        }
        return prev;
    }

    ListNode reverseListRecursive(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }

        ListNode newHead = reverseListRecursive(head.next);
        head.next.next = head;
        head.next = null;
        return newHead;
    }
}
