package com.example.demo.apps.leetcode.sliding_window;

import java.util.HashMap;
import java.util.Map;

import static java.lang.IO.println;

/**
 * Sliding window (<a href="https://leetcode.com/problems/longest-substring-without-repeating-characters/">3</a>)
 * <a href="https://algo.monster/liteproblems/3">3</a>
 * <h2>Longest Substring Without Repeating Characters</h2>
 * Given a string s, find the length of the longest substring without duplicate characters.
 *
 * Example 1:
 *
 * Input: s = "abcabcbb"
 * Output: 3
 * Explanation: The answer is "abc", with the length of 3. Note that "bca" and "cab" are also correct answers.
 * Example 2:
 *
 * Input: s = "bbbbb"
 * Output: 1
 * Explanation: The answer is "b", with the length of 1.
 * Example 3:
 *
 * Input: s = "pwwkew"
 * Output: 3
 * Explanation: The answer is "wke", with the length of 3.
 * Notice that the answer must be a substring, "pwke" is a subsequence and not a substring.
 */
public class LongestSubstringWithoutRepeatingCharacters {
    void main() {
        String s = "abcabcbb";
        println(lengthOfLongestSubstring(s));
    }

    int lengthOfLongestSubstring(String s) {
        Map<Character, Integer> map = new HashMap<>();
        int maxLen = 0, left = 0;
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            if (map.containsKey(c) && map.get(c) >= left) {
                left = map.get(c) + 1;
            }
            map.put(c, right);
            maxLen = Math.max(maxLen, right - left + 1);
        }
        return maxLen;
    }
}
