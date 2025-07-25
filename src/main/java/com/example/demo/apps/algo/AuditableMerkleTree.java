package com.example.demo.apps.algo;

import lombok.Getter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.*;

@Getter
public class AuditableMerkleTree {

    private static final ForkJoinPool POOL = new ForkJoinPool();

    // Stores all levels: level 0 = leaves, level N = root
    private final List<List<String>> levels = new ArrayList<>();

    public String computeMerkleRoot(List<String> dataBlocks) {
        if (dataBlocks.isEmpty()) return "";

        // Level 0: hash all leaves
        List<String> currentLevel = POOL.invoke(new ParallelHashTask(dataBlocks, 0, dataBlocks.size()));
        levels.add(currentLevel);

        // Build and store each level
        while (currentLevel.size() > 1) {
            List<String> nextLevel = buildNextLevelParallel(currentLevel);
            levels.add(nextLevel);
            currentLevel = nextLevel;
        }

        return currentLevel.getFirst();
    }

    private List<String> buildNextLevelParallel(List<String> level) {
        int pairCount = (level.size() + 1) / 2;
        return POOL.invoke(new ParallelPairHashTask(level, 0, pairCount));
    }

    // ForkJoin tasks same as before
    static class ParallelHashTask extends RecursiveTask<List<String>> {
        private static final int THRESHOLD = 10_000;
        List<String> data;
        int start, end;

        ParallelHashTask(List<String> data, int start, int end) {
            this.data = data;
            this.start = start;
            this.end = end;
        }

        @Override
        protected List<String> compute() {
            int length = end - start;
            if (length <= THRESHOLD) {
                List<String> result = new ArrayList<>(length);
                for (int i = start; i < end; i++) {
                    result.add(hash(data.get(i)));
                }
                return result;
            } else {
                int mid = start + length / 2;
                ParallelHashTask left = new ParallelHashTask(data, start, mid);
                ParallelHashTask right = new ParallelHashTask(data, mid, end);
                invokeAll(left, right);
                List<String> merged = new ArrayList<>(length);
                merged.addAll(left.join());
                merged.addAll(right.join());
                return merged;
            }
        }
    }

    static class ParallelPairHashTask extends RecursiveTask<List<String>> {
        private static final int THRESHOLD = 5000;
        List<String> nodes;
        int start, pairCount;

        ParallelPairHashTask(List<String> nodes, int start, int pairCount) {
            this.nodes = nodes;
            this.start = start;
            this.pairCount = pairCount;
        }

        @Override
        protected List<String> compute() {
            if (pairCount <= THRESHOLD) {
                List<String> result = new ArrayList<>(pairCount);
                for (int i = 0; i < pairCount; i++) {
                    int leftIdx = 2 * i;
                    int rightIdx = Math.min(leftIdx + 1, nodes.size() - 1);
                    String left = nodes.get(leftIdx);
                    String right = nodes.get(rightIdx);
                    result.add(hash(left + right));
                }
                return result;
            } else {
                int mid = pairCount / 2;
                ParallelPairHashTask left = new ParallelPairHashTask(nodes, start, mid);
                ParallelPairHashTask right = new ParallelPairHashTask(nodes, start + mid, pairCount - mid);
                invokeAll(left, right);
                List<String> merged = new ArrayList<>(pairCount);
                merged.addAll(left.join());
                merged.addAll(right.join());
                return merged;
            }
        }
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
    // Example usage
    public static void main(String[] args) {
        int count = 16;
        List<String> blocks = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            blocks.add("Block #" + i);
        }

        AuditableMerkleTree tree = new AuditableMerkleTree();
        String root = tree.computeMerkleRoot(blocks);
        System.out.println("Merkle Root: " + root);

        // Print all levels for audit
        List<List<String>> levels = tree.getLevels();
        for (int i = 0; i < levels.size(); i++) {
            System.out.println("Level " + i + " (" + levels.get(i).size() + " nodes):");
            for (String hash : levels.get(i)) {
                System.out.println("  " + hash);
            }
        }
    }
}
