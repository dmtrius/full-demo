package com.example.demo.apps.bloom;

import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.stream.IntStream;

@Slf4j
public class BloomFilterTest {
    void main() {
        int n = 100_000;
        double targetFpp = 0.01;

        BloomFilter<String> bf = BloomFilter.create(n, targetFpp);
        IntStream.range(0, n).forEach(i -> bf.add("key-" + i));

        // 1. No false negatives
        long falseNegatives = IntStream.range(0, n)
                .filter(i -> !bf.mightContain("key-" + i)).count();
        log.info("False negatives: {} (must be 0)", falseNegatives);

        // 2. Measured FPP against unseen keys
        int trials = 100_000;
        long falsePositives = IntStream.range(0, trials)
                .mapToObj(i -> UUID.randomUUID().toString())
                .filter(bf::mightContain).count();
        double measured = (double) falsePositives / trials;
        System.out.printf("Target FPP: %.4f, estimated by filter: %.4f, measured: %.4f%n",
                targetFpp, bf.estimatedFalsePositiveProbability(), measured);

        // 3. Sizing
        System.out.printf("Bits: %d (%.1f per element), hashes: k derived internally%n",
                bf.bitCount(), (double) bf.bitCount() / n);

        if (falseNegatives != 0 || measured > targetFpp * 1.5)
            throw new AssertionError("Bloom filter quality check failed");
        IO.println("ALL CHECKS PASSED");
    }
}
