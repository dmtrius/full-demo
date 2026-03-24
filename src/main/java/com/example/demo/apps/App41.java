package com.example.demo.apps;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static java.lang.IO.println;

public class App41 {
    void main() {
        println(lengthOfLongestSubstring("abcabcbb"));
        println(lengthOfLongestSubstring("bbbbb"));
        println(lengthOfLongestSubstring("pwwkew"));
    }

    int lengthOfLongestSubstring(String s) {
        if (Objects.isNull(s) || s.isBlank()) {
            return 0;
        }

        Set<Character> seen = new HashSet<>();
        int left = 0;
        int maxLength = 0;

        for (int right = 0; right < s.length(); ++right) {
            char c = s.charAt(right);
            while (seen.contains(c)) {
                seen.remove(s.charAt(left));
                ++left;
            }
            seen.add(c);
            maxLength = Math.max(maxLength, right - left + 1);
        }

        return maxLength;
    }
}

/*
Given a string s, find the length of the longest substring without repeating characters.
Input:  s = "abcabcbb"
Output: 3
Explanation: "abc" is the longest substring without repeating characters.
Input:  s = "bbbbb"
Output: 1
Explanation: "b"
Input:  s = "pwwkew"
Output: 3
Explanation: "wke"
 */