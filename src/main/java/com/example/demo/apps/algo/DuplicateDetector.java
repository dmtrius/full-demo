package com.example.demo.apps.algo;

import java.util.BitSet;
import java.util.Random;

public class DuplicateDetector {

    private final BloomFilter bloomFilter;
    private final BitSet seenDefinitely; // To reduce false positives (optional)

    // Constructor
    public DuplicateDetector(int expectedElements, double falsePositiveRate) {
        this.bloomFilter = new BloomFilter(expectedElements, falsePositiveRate);
        // Optional: Use a small hash set to store elements that triggered bloom filter
        // This helps reduce false positives. Only store elements we've seen for sure.
        this.seenDefinitely = new BitSet();
    }

    // Detect if number is duplicate
    public boolean isDuplicate(int number) {
        if (!bloomFilter.mightContain(number)) {
            bloomFilter.add(number);
            return false; // Not a duplicate
        } else {
            // Bloom filter says "might be duplicate"
            // Check if we've seen it for sure
            if (seenDefinitely.get(hash(number))) {
                return true; // Definitely a duplicate
            } else {
                // False positive likely; mark it as seen now
                seenDefinitely.set(hash(number));
                return false; // Not a duplicate, but bloom filter triggered
            }
        }
    }

    // Simple hash function for BitSet indexing
    private int hash(int number) {
        return Math.abs(number) % seenDefinitely.size();
    }

    // Inner class: Bloom Filter
    private static class BloomFilter {
        private final BitSet bitSet;
        private final int numHashFunctions;
        private final int bitSetSize;
        private final Random random;

        public BloomFilter(int expectedElements, double falsePositiveRate) {
            this.bitSetSize = (int) Math.ceil((expectedElements * Math.log(falsePositiveRate)) / Math.log(1.0 / (Math.pow(2.0, Math.log(2.0)))));
            this.numHashFunctions = Math.max(1, (int) Math.round(Math.log(2.0) * bitSetSize / expectedElements));
            this.bitSet = new BitSet(bitSetSize);
            this.random = new Random(42); // Fixed seed for consistent hashing
        }

        public void add(int element) {
            for (int i = 0; i < numHashFunctions; i++) {
                int hash = hash(element, i);
                bitSet.set(hash);
            }
        }

        public boolean mightContain(int element) {
            for (int i = 0; i < numHashFunctions; i++) {
                int hash = hash(element, i);
                if (!bitSet.get(hash)) {
                    return false;
                }
            }
            return true;
        }

        private int hash(int element, int seed) {
            random.setSeed(element ^ (seed * 92821L));
            return random.nextInt(bitSetSize);
        }
    }

    // Example usage
    @SuppressWarnings("unused")
    public static void main(String[] args) {
        // Assume we expect ~1 million elements, with 1% false positive rate
        DuplicateDetector detector = new DuplicateDetector(1_000_000, 0.01);

        int[] stream = {1, 2, 3, 4, 2, 5, 3, 6, 1, 7};

        System.out.println("Duplicate numbers:");
        for (int num : stream) {
            if (detector.isDuplicate(num)) {
                System.out.println(num + " (duplicate)");
            }
        }
    }
}
