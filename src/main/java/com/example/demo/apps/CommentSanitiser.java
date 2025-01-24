package com.example.demo.apps;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommentSanitiser {

    // List of swear words to be replaced
    private static final String[] SWEAR_WORDS = {"crap", "cretin"};

    public static void main(String[] args) {
        String comment = """
            James was a crap builder and generally a cretin. Send him your complaints on james@jollycleverbuilders.com.";
        """;
        String sanitisedComment = sanitiseComment(comment);
        System.out.println(sanitisedComment);
    }

    public static String sanitiseComment(String comment) {
        // Replace email addresses
        comment = replaceEmailAddresses(comment);

        // Replace swear words
        comment = replaceSwearWords(comment);

        return comment;
    }

    private static String replaceEmailAddresses(String comment) {
        // Regular expression to match email addresses
        String emailPattern = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(comment);

        // Replace all email addresses with <email address>
        return matcher.replaceAll("<email address>");
    }

    private static String replaceSwearWords(String comment) {
        for (String swearWord : SWEAR_WORDS) {
            // Create a pattern to match the swear word
            Pattern pattern = Pattern.compile("\\b" + swearWord + "\\b", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(comment);

            // Replace the swear word with asterisks
            comment = matcher.replaceAll(createAsterisks(swearWord.length()));
        }
        return comment;
    }

    private static String createAsterisks(int length) {
        // Create a string of asterisks of the given length
        return "*".repeat(length);
    }
}
