package com.example.demo.apps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class App9 {
    public static void main(String[] args) {
        String initial = "This is an example of text wrapping in Java";
        String[] words = initial.split("\\s");
        int lineWidth = 15;

        List<String> wrappedLines = wrapWords(words, lineWidth);
        String result = String.join("\n", wrappedLines);
        System.out.println(result);
    }

    public static void breakWords() {
        int maxSize = 10;
        String str = "ENwMFigy8vC jnj jnjnjn jnjnj njnjnjn jnj hbhubu buuhbuhbuhb hbhbuhh";
        String[] words = str.split("\\s");
        StringBuilder result = new StringBuilder();
        int length = 0;

        for (String word : words) {
            length += word.length() + 1;
            if (length <= maxSize) {
                result
                        .append(word)
                        .append(" ");
            } else {
                length = 0;
                result.append("\n");
            }
        }

        System.out.println(result);
    }

    public static List<String> wrapWords(String[] words, int lineWidth) {
        List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        int currentLineLength = 0;

        for (String word : words) {
            if (currentLineLength + word.length() + (currentLineLength > 0 ? 1 : 0) > lineWidth) {
                // Add the current line to the list of lines
                lines.add(currentLine.toString().trim());
                // Start a new line
                currentLine = new StringBuilder();
                currentLineLength = 0;
            }
            if (currentLineLength > 0) {
                currentLine.append(" ");
                currentLineLength++;
            }
            currentLine.append(word);
            currentLineLength += word.length();
        }

        // Add the last line
        if (currentLineLength > 0) {
            lines.add(currentLine.toString().trim());
        }

        return lines;
    }
}

//Given a sequence of words, and a limit on the number of characters that can be put in one line (line width). Put line breaks in the given sequence such that the lines are printed neatly. Assume that the length of each word is smaller than the line width.