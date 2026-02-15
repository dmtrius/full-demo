package com.example.demo.apps.leetcode.fast_slow_pointers;

import com.example.demo.apps.leetcode.ListNode;

import java.util.HashSet;
import java.util.Set;

import static com.example.demo.apps.leetcode.Utils.createCycle;
import static com.example.demo.apps.leetcode.Utils.createList;
import static java.lang.IO.println;

/**
 * Fast & Slow Pointers (<a href="https://leetcode.com/problems/linked-list-cycle/">141</a>)
 * <a href="https://algo.monster/liteproblems/141">141</a>
 * <h2>Linked List Cycle</h2>
 * Given head, the head of a linked list, determine if the linked list has a cycle in it.
 * <p>
 * There is a cycle in a linked list if there is some node in the list that can be reached again by continuously following the next pointer. Internally, pos is used to denote the index of the node that tail's next pointer is connected to. Note that pos is not passed as a parameter.
 * <p>
 * Return true if there is a cycle in the linked list. Otherwise, return false.
 * <p>
 * Example 1:
 * <p>
 * Input: head = [3,2,0,-4], pos = 1
 * Output: true
 * Explanation: There is a cycle in the linked list, where the tail connects to the 1st node (0-indexed).
 * Example 2:
 * <p>
 * Input: head = [1,2], pos = 0
 * Output: true
 * Explanation: There is a cycle in the linked list, where the tail connects to the 0th node.
 * Example 3:
 * <p>
 * Input: head = [1], pos = -1
 * Output: false
 * Explanation: There is no cycle in the linked list.
 * <p>
 * Constraints:
 * <p>
 * The number of the nodes in the list is in the range [0, 104].
 * -105 <= Node.val <= 105
 * pos is -1 or a valid index in the linked-list.
 * <p>
 * Follow up: Can you solve it using O(1) (i.e. constant) memory?
 */
public class LinkedListCycle {
    void main() {
        int[] nums = {3, 2, 0, -4};
        var head = createList(nums);
        println(hasCycleFloyd(head));
        println(hasCycleHashSet(head));
        createCycle(head, 2);
        println(hasCycleHashSet(head));
        println(hasCycleFloyd(head));
    }

    boolean hasCycleFloyd(ListNode head) {
        if (head == null || head.next == null) {
            return false;
        }

        ListNode slow = head;
        ListNode fast = head;

        while (fast != null && fast.next != null) {
            slow = slow.next;        // Move 1 step
            fast = fast.next.next;   // Move 2 steps

            if (slow == fast) {
                return true;         // Cycle detected
            }
        }

        return false;  // Reached end, no cycle
    }

    boolean hasCycleHashSet(ListNode head) {
        Set<ListNode> visited = new HashSet<>();

        ListNode current = head;
        while (current != null) {
            if (visited.contains(current)) {
                return true;  // Node visited before, cycle exists
            }
            visited.add(current);
            current = current.next;
        }

        return false;
    }
}
