package com.example.demo.apps;

import java.util.LinkedList;
import java.util.List;

import static java.lang.IO.println;

public class App41 {
    void main() {
        println(longestStringWithNoRepetition("abcabcbb"));
        println(longestStringWithNoRepetition("bbbbb"));
        println(longestStringWithNoRepetition("pwwkew"));
    }

    int longestStringWithNoRepetition(String s) {
        char[] input = s.toCharArray();
        int start = 0;
        int end = 1;
        char current = input[0];
        List<Character> present = new LinkedList<>();
        present.add(current);
        int longest = end - start;
        for (int i = 1; i < input.length; ++i) {
            current = input[i];
            end = i;
            if (!present.contains(current)) {
                present.add(current);
                if (longest < end - start) {
                    longest = end - start;
                }
            } else {
                start++;
                present.clear();
            }
        }
        if (!present.isEmpty()) {
            return end - start;
        } else {
            return 0;
        }
    }
}

/**
 * “Given a string s, find the length of the longest substring without repeating characters.
 *
 * Input:  s = "abcabcbb"
 *
 * Output: 3
 *
 * Explanation: "abc" is the longest substring without repeating characters.
 *
 * Input:  s = "bbbbb"
 *
 * Output: 1
 *
 * Explanation: "b"
 *
 * Input:  s = "pwwkew"
 *
 * Output: 3
 *
 * Explanation: "wke"
 *
 */