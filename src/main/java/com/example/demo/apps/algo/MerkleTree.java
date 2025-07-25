package com.example.demo.apps.algo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class MerkleTree {

    public static String buildMerkleRoot(List<String> dataBlocks) {
        List<String> currentLevel = new ArrayList<>();
        for (String data : dataBlocks) {
            currentLevel.add(hash(data));
        }

        while (currentLevel.size() > 1) {
            currentLevel = buildNextLevel(currentLevel);
        }

        return currentLevel.getFirst(); // Merkle Root
    }

    private static List<String> buildNextLevel(List<String> hashes) {
        List<String> nextLevel = new ArrayList<>();

        int i = 0;
        while (i < hashes.size()) {
            String left = hashes.get(i);
            String right = (i + 1 < hashes.size()) ? hashes.get(i + 1) : left; // duplicate if odd
            String combinedHash = hash(left + right);
            nextLevel.add(combinedHash);
            i += 2;
        }

        return nextLevel;
    }

    public static String hash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(data.getBytes());
            return bytesToHex(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not supported", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    // Test method
    @SuppressWarnings("unused")
    public static void main(String[] args) {
        List<String> dataBlocks = List.of("Block A", "Block B", "Block C", "Block D");
        String merkleRoot = buildMerkleRoot(dataBlocks);
        System.out.println("Merkle Root: " + merkleRoot);
    }
}
