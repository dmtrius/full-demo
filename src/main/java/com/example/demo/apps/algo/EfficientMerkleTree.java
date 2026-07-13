package com.example.demo.apps.algo;

import org.apache.commons.lang.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class EfficientMerkleTree {

    public static String computeMerkleRoot(Iterator<String> dataIterator) {
        List<String> currentLevel = new ArrayList<>();

        // Hash all data blocks as leaf nodes
        while (dataIterator.hasNext()) {
            String data = dataIterator.next();
            currentLevel.add(hash(data));
        }

        // Handle an empty tree case
        if (currentLevel.isEmpty()) {
            return StringUtils.EMPTY;
        }

        // Build tree level-by-level
        while (currentLevel.size() > 1) {
            List<String> nextLevel = new ArrayList<>((currentLevel.size() + 1) / 2);

            for (int i = 0; i < currentLevel.size(); i += 2) {
                String left = currentLevel.get(i);
                String right = (i + 1 < currentLevel.size()) ? currentLevel.get(i + 1) : left;
                nextLevel.add(hash(left + right));
            }

            // Discard previous level to save memory
            currentLevel = nextLevel;
        }

        return currentLevel.getFirst(); // Merkle root
    }

    public static String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(input.getBytes());
            return bytesToHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }

    @SuppressWarnings("unused")
    // Demo: simulate a large dataset
    void main() {
        int count = 1_000_000; // 1 million entries
        Iterator<String> data = new Iterator<>() {
            int i = 0;
            @Override
            public boolean hasNext() {
                return i < count;
            }
            @Override
            public String next() {
                if (hasNext()) {
                    return "Block #" + (i++);
                }
                throw new NoSuchElementException();
            }
        };

        long start = System.currentTimeMillis();
        String root = computeMerkleRoot(data);
        long end = System.currentTimeMillis();

        IO.println("Merkle Root: " + root);
        IO.println("Computed in " + (end - start) + " ms");
    }
}
