package com.example.demo.apps;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.lang.IO.println;

public class App35 {
    void main() {
        String input = "ABC";
        println("Number of unique substrings: " + countUniqueSubstrings(input));
        println("Sum of unique chars in all unique substrings: " + sumUniqueCharsInSubstrings(input));
    }

    public static int countUniqueSubstrings(String s) {
        TrieNode root = new TrieNode(new HashMap<>());
        int count = 0;

        for (int i = 0; i < s.length(); i++) {
            TrieNode current = root;
            for (int j = i; j < s.length(); j++) {
                char c = s.charAt(j);
                if (!current.children().containsKey(c)) {
                    current.children().put(c, new TrieNode(new HashMap<>()));
                    ++count;
                }
                current = current.children().get(c);
            }
        }

        return count;
    }

    public static int sumUniqueCharsInSubstrings(String s) {
        Set<String> uniqueSubstrings = new HashSet<>();
        int total = 0;

        // Generate all unique substrings
        for (int i = 0; i < s.length(); i++) {
            StringBuilder current = new StringBuilder();
            for (int j = i; j < s.length(); j++) {
                current.append(s.charAt(j));
                uniqueSubstrings.add(current.toString());
            }
        }

        // For each unique substring, count unique chars and sum
        for (String substr : uniqueSubstrings) {
            Set<Character> uniqueChars = new HashSet<>();
            for (char c : substr.toCharArray()) {
                uniqueChars.add(c);
            }
            total += uniqueChars.size();
        }

        println(uniqueSubstrings);

        return total;
    }
}

record TrieNode(Map<Character, TrieNode> children) {}
