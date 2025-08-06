package com.example.demo.apps.algo;

import static java.io.IO.println;

public class SubsetSum {
    public static boolean isSubsetSum(int[] set, int n, int sum) {
        if (sum == 0) return true;
        if (n == 0) return false;

        if (set[n - 1] > sum)
            return isSubsetSum(set, n - 1, sum);

        return isSubsetSum(set, n - 1, sum) ||
                isSubsetSum(set, n - 1, sum - set[n - 1]);
    }

    @SuppressWarnings("preview")
    void main() {
        int[] set = {3, 34, 4, 12, 5, 2};
        int sum = 9;
        if (isSubsetSum(set, set.length, sum)) {
            println("Found a subset with given sum: " + sum);
        } else {
            println("No subset with given sum: " + sum);
        }
    }
}
