package com.example.demo.apps;

import java.math.BigDecimal;

public class App16 {
    public static void main(String... args) {
        //testPrimes();
        BigDecimal a = new BigDecimal("10.00");
        BigDecimal b = new BigDecimal("10.0");
        System.out.println(a.equals(b)); // false (different scales)
        System.out.println(a.compareTo(b) == 0); // true (same value)
    }

    private static void testPrimes() {
        // Example benchmark
        long start = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            isPrime(104729); // Test with a large prime number
        }
        long end = System.nanoTime();
        System.out.println("Time taken: " + (end - start) / 1000000.0 + " ms");
    }

    private static boolean isPrime(int n) {
        // Optimize common non-prime cases
        if (n < 2) return false;
        if (n == 2 || n == 3) return true;
        if ((n & 1) == 0) return false;  // Bitwise check for even numbers
        if (n % 3 == 0) return false;

        // Use int for sqrt calculation instead of repeated multiplication
        int sqrt = (int) Math.sqrt(n);

        // Optimize loop: check only numbers of form 6k±1 up to sqrt
        // This works because all primes > 3 can be expressed as 6k±1
        for (int i = 5; i <= sqrt; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) {
                return false;
            }
        }

        return true;
    }
}
