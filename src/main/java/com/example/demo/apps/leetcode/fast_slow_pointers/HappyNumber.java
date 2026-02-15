package com.example.demo.apps.leetcode.fast_slow_pointers;

import java.util.HashSet;
import java.util.Set;

import static java.lang.IO.println;

/**
 * Fast & Slow Pointers (<a href="https://leetcode.com/problems/happy-number/">202</a>)
 * <a href="https://algo.monster/liteproblems/202">202</a>
 * <h2>Happy Number</h2>
 * Write an algorithm to determine if a number n is happy.
 * <p>
 * A happy number is a number defined by the following process:
 * <p>
 * Starting with any positive integer, replace the number by the sum of the squares of its digits.
 * Repeat the process until the number equals 1 (where it will stay), or it loops endlessly in a cycle which does not include 1.
 * Those numbers for which this process ends in 1 are happy.
 * Return true if n is a happy number, and false if not.
 * <p>
 * Example 1:
 * <p>
 * Input: n = 19
 * Output: true
 * Explanation:
 * 12 + 92 = 82
 * 82 + 22 = 68
 * 62 + 82 = 100
 * 12 + 02 + 02 = 1
 * Example 2:
 * <p>
 * Input: n = 2
 * Output: false
 */
public class HappyNumber {
    void main() {
        println(isHappyHashSet(19));
        println(isHappyHashSet(18));
        println(isHappyFloyd(19));
        println(isHappyFloyd(18));
    }

    boolean isHappyHashSet(int n) {
        Set<Integer> seen = new HashSet<>();
        while (n != 1 && !seen.contains(n)) {
            seen.add(n);
            int sum = 0;
            while (n > 0) {
                int digit = n % 10;
                sum += digit * digit;
                n /= 10;
            }
            n = sum;
        }
        return n == 1;
    }

    boolean isHappyFloyd(int n) {
        int slow = n, fast = getSum(n);
        while (slow != fast) {
            slow = getSum(slow);
            fast = getSum(getSum(fast));
        }
        return slow == 1;
    }

    private int getSum(int n) {
        int sum = 0;
        while (n > 0) {
            int digit = n % 10;
            sum += digit * digit;
            n /= 10;
        }
        return sum;
    }
}
