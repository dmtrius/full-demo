package com.example.demo.apps.algo;

import java.util.Arrays;

public class DualPivotQuicksort {
    public static void sort(int[] arr) {
        if (arr == null || arr.length == 0) {
            return;
        }
        dualPivotQuicksort(arr, 0, arr.length - 1);
    }

    private static void dualPivotQuicksort(int[] arr, int left, int right) {
        if (right <= left) {
            return;
        }

        // If the left element is greater than the right element, swap them
        if (arr[left] > arr[right]) {
            swap(arr, left, right);
        }

        // Store the pivot elements
        int pivot1 = arr[left];      // First pivot (smaller)
        int pivot2 = arr[right];     // Second pivot (larger)

        // Partitioning variables
        int lt = left + 1;           // Elements less than pivot1
        int gt = right - 1;          // Elements greater than pivot2
        int i = lt;                  // Scanning pointer

        // Partitioning process
        while (i <= gt) {
            // If the current element is less than the first pivot
            if (arr[i] < pivot1) {
                swap(arr, i, lt);
                lt++;
                i++;
            }
            // If the current element is greater than the second pivot
            else if (arr[i] > pivot2) {
                swap(arr, i, gt);
                gt--;
            }
            // If the current element is between the pivots
            else {
                i++;
            }
        }

        // Move pivots to their final positions
        lt--;
        gt++;
        swap(arr, left, lt);
        swap(arr, right, gt);

        // Recursively sort the three partitions
        dualPivotQuicksort(arr, left, lt - 1);    // Sort elements less than pivot1
        dualPivotQuicksort(arr, lt + 1, gt - 1);  // Sort elements between pivots
        dualPivotQuicksort(arr, gt + 1, right);   // Sort elements greater than pivot2
    }

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    // Example usage
    public static void main(String[] args) {
        int[] arr = {24, 8, 42, 75, 29, 77, 38, 57, 88, 91, 3, 99, 66};
        System.out.println("Original array: " + Arrays.toString(arr));

        sort(arr);

        System.out.println("Sorted array: " + Arrays.toString(arr));
    }
}