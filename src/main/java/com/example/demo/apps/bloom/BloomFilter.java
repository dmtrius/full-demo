package com.example.demo.apps.bloom;

import java.io.Serial;
import java.io.Serializable;
import java.util.BitSet;
import java.util.Objects;

/**
 * A probabilistic set membership data structure.
 *
 * <p>Guarantees:
 * <ul>
 *   <li>No false negatives — if {@code contains(x)} is false, {@code x} was never added.</li>
 *   <li>False positives possible at a rate bounded by the configured target.</li>
 * </ul>
 *
 * <p>Design notes:
 * <ul>
 *   <li>Optimal bit count ({@code m}) and hash count ({@code k}) are derived from
 *       expected insertions {@code n} and target false-positive probability {@code p}:
 *       {@code m = -n·ln(p) / (ln 2)²}, {@code k = (m/n)·ln 2}.</li>
 *   <li>Double hashing (Kirsch–Mitzenmacher, 2006) generates {@code k} positions
 *       from two independent 64-bit hashes, so only one hashing pass per element.</li>
 *   <li>Hashes are split-mix based finalizers over a seeded 64-bit mixer, avoiding
 *       {@code Object#hashCode}'s 31-bit weaknesses.</li>
 *   <li>Instances are <em>not</em> thread-safe; wrap with {@link SynchronizedBloomFilter}
 *       or guard externally for concurrent use.</li>
 * </ul>
 *
 * @param <T> element type
 */
public final class BloomFilter<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final double LN2 = Math.log(2);
    private static final double LN2_SQUARED = LN2 * LN2;

    private final BitSet bits;
    private final int bitCount;      // m
    private final int hashCount;     // k
    private final long seed;
    private final Funnel<? super T> funnel;
    private int insertions;

    /**
     * Strategy for extracting canonical bytes from an element.
     * Must be deterministic and consistent across JVM runs if the filter
     * is persisted or shared between processes.
     */
    @FunctionalInterface
    public interface Funnel<T> extends Serializable {
        long hash64(T value, long seed);

        /** Default funnel: mixes {@code Objects.hashCode} via splitmix64. */
        static <T> Funnel<T> defaultFunnel() {
            return (value, seed) -> mix64(Objects.hashCode(value) ^ seed);
        }

        private static long mix64(long z) {
            z = (z ^ (z >>> 30)) * 0xbf58476d1ce4e5b9L;
            z = (z ^ (z >>> 27)) * 0x94d049bb133111ebL;
            return z ^ (z >>> 31);
        }
    }

    private BloomFilter(int bitCount, int hashCount, long seed, Funnel<? super T> funnel) {
        this.bitCount = bitCount;
        this.hashCount = hashCount;
        this.seed = seed;
        this.funnel = funnel;
        this.bits = new BitSet(bitCount);
    }

    /**
     * Creates a filter sized for {@code expectedInsertions} elements at the given
     * target false-positive probability (e.g. {@code 0.01} for 1%).
     */
    public static <T> BloomFilter<T> create(int expectedInsertions,
                                            double falsePositiveProbability,
                                            Funnel<? super T> funnel) {
        if (expectedInsertions <= 0)
            throw new IllegalArgumentException("expectedInsertions must be positive");
        if (!(falsePositiveProbability > 0.0 && falsePositiveProbability < 1.0))
            throw new IllegalArgumentException("fpp must be in (0, 1): " + falsePositiveProbability);
        Objects.requireNonNull(funnel, "funnel");

        long m = optimalBitCount(expectedInsertions, falsePositiveProbability);
        if (m > Integer.MAX_VALUE)
            throw new IllegalArgumentException("required bit count exceeds addressable size: " + m);
        int k = optimalHashCount((int) m, expectedInsertions);
        return new BloomFilter<>((int) m, k, 0x9E3779B97F4A7C15L, funnel);
    }

    public static <T> BloomFilter<T> create(int expectedInsertions, double falsePositiveProbability) {
        return create(expectedInsertions, falsePositiveProbability, Funnel.defaultFunnel());
    }

    /** Optimal number of bits: m = -n·ln(p) / (ln 2)². */
    static long optimalBitCount(long n, double p) {
        return (long) Math.ceil(-n * Math.log(p) / LN2_SQUARED);
    }

    /** Optimal number of hash functions: k = (m/n)·ln 2. */
    static int optimalHashCount(int m, long n) {
        return Math.max(1, (int) Math.round((double) m / n * LN2));
    }

    /**
     * Adds an element. After this call, {@link #mightContain} for the same
     * element is guaranteed to return {@code true}.
     */
    public void add(T value) {
        Objects.requireNonNull(value, "value");
        long h1 = funnel.hash64(value, seed);
        long h2 = Funnel.defaultFunnel().hash64(value, seed ^ 0x517cc1b727220a95L);
        for (int i = 0; i < hashCount; i++) {
            // Double hashing: h_i = h1 + i·h2; rotation avoids clustering when h2 is even.
            long combined = h1 + i * h2;
            if (i > 0) combined = Long.rotateLeft(combined, i);
            bits.set(index(combined));
        }
        insertions++;
    }

    /**
     * Returns {@code true} if the element was probably added,
     * {@code false} if it definitely was not.
     */
    public boolean mightContain(T value) {
        Objects.requireNonNull(value, "value");
        long h1 = funnel.hash64(value, seed);
        long h2 = Funnel.defaultFunnel().hash64(value, seed ^ 0x517cc1b727220a95L);
        for (int i = 0; i < hashCount; i++) {
            long combined = h1 + i * h2;
            if (i > 0) combined = Long.rotateLeft(combined, i);
            if (!bits.get(index(combined))) return false;
        }
        return true;
    }

    /** Maps a 64-bit hash into [0, bitCount) using unsigned remainder. */
    private int index(long hash) {
        return (int) Long.remainderUnsigned(hash, bitCount);
    }

    /** Number of elements added (approximate membership basis). */
    public int insertions() {
        return insertions;
    }

    /** Configured bit array size (m). */
    public int bitCount() {
        return bitCount;
    }

    /**
     * Estimates the current false-positive probability given actual insertions:
     * (1 - e^(-k·n/m))^k.
     */
    public double estimatedFalsePositiveProbability() {
        return Math.pow(1 - Math.exp(-(double) hashCount * insertions / bitCount), hashCount);
    }

    /** Thread-safe decorator using intrinsic locking on all operations. */
    public static final class SynchronizedBloomFilter<T> {
        private final BloomFilter<T> delegate;

        public SynchronizedBloomFilter(BloomFilter<T> delegate) {
            this.delegate = Objects.requireNonNull(delegate);
        }

        public synchronized void add(T value) {
            delegate.add(value);
        }

        public synchronized boolean mightContain(T value) {
            return delegate.mightContain(value);
        }

        public synchronized double estimatedFalsePositiveProbability() {
            return delegate.estimatedFalsePositiveProbability();
        }
    }
}
