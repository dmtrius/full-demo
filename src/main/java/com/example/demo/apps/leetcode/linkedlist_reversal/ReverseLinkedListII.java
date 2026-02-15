package com.example.demo.apps.leetcode.linkedlist_reversal;

import com.example.demo.apps.leetcode.ListNode;

import static com.example.demo.apps.leetcode.Utils.createList;
import static java.lang.IO.println;

/**
 * LinkedList In-Place Reversal (<a href="https://leetcode.com/problems/reverse-linked-list-ii/">92</a>)
 * <a href="https://algo.monster/liteproblems/92">92</a>
 * <h2>Reverse Linked List II</h2>
 * Given the head of a singly linked list and two integers left and right where left <= right, reverse the nodes of the list from position left to position right, and return the reversed list.
 * <p>
 * Example 1:
 * <p>
 * Input: head = [1,2,3,4,5], left = 2, right = 4
 * Output: [1,4,3,2,5]
 * Example 2:
 * <p>
 * Input: head = [5], left = 1, right = 1
 * Output: [5]
 */
public class ReverseLinkedListII {
    void main() {
        int[] nums = {3, 2, 0, -4, 10, -4, 11};
        var head = createList(nums);
        println(head);
        reverseBetween(head, 2, 4);
        println(head);
        reverseBetweenAlternative(head, 2, 4);
        println(head);
        reverseBetweenRecursive(head, 2, 4);
        println(head);
    }

    ListNode reverseBetween(ListNode head, int left, int right) {
        // Edge case: no reversal needed
        if (head == null || left == right) {
            return head;
        }

        // Create dummy node to handle edge case when left = 1
        ListNode dummy = new ListNode(0);
        dummy.next = head;

        // Step 1: Move to the node just BEFORE position 'left'
        ListNode prev = dummy;
        for (int i = 1; i < left; i++) {
            prev = prev.next;
        }

        // Step 2: Reverse the sublist from 'left' to 'right'
        // current: the node at position 'left' (will end up at position 'right')
        ListNode current = prev.next;

        // Perform (right - left) reversals
        for (int i = 0; i < right - left; i++) {
            ListNode nextNode = current.next;
            current.next = nextNode.next;
            nextNode.next = prev.next;
            prev.next = nextNode;
        }

        return dummy.next;
    }

    ListNode reverseBetweenAlternative(ListNode head, int left, int right) {
        if (head == null || left == right) return head;

        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode prev = dummy;

        // Navigate to node before 'left'
        for (int i = 1; i < left; i++) {
            prev = prev.next;
        }

        // Reverse the sublist using standard technique
        ListNode tail = prev.next;  // Will become tail after reversal
        ListNode curr = tail.next;

        for (int i = 0; i < right - left; i++) {
            tail.next = curr.next;
            curr.next = prev.next;
            prev.next = curr;
            curr = tail.next;
        }

        return dummy.next;
    }

    private ListNode successor = null;

    ListNode reverseBetweenRecursive(ListNode head, int left, int right) {
        // Base case: start reversing from position 1
        if (left == 1) {
            return reverseN(head, right);
        }

        // Recursively move to the starting position
        head.next = reverseBetween(head.next, left - 1, right - 1);
        return head;
    }

    // Reverse first N nodes
    private ListNode reverseN(ListNode head, int n) {
        if (n == 1) {
            successor = head.next;
            return head;
        }

        ListNode last = reverseN(head.next, n - 1);
        head.next.next = head;
        head.next = successor;

        return last;
    }
}
