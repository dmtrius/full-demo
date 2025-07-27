package com.example.demo.apps.algo;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.Arrays;
import java.util.Random;

/**
 * Parallel QuickSort implementation using Java's Fork-Join framework
 */
public class ParallelQuickSort extends RecursiveAction {
    private final int[] array;
    private final int low;
    private final int high;
    private static final int THRESHOLD = 1000; // Switch to sequential sort for small arrays

    public ParallelQuickSort(int[] array, int low, int high) {
        this.array = array;
        this.low = low;
        this.high = high;
    }

    @Override
    protected void compute() {
        if (high - low < THRESHOLD) {
            // Use sequential quicksort for small subarrays
            sequentialQuickSort(array, low, high);
        } else if (low < high) {
            // Partition the array and get pivot index
            int pivotIndex = partition(array, low, high);

            // Create subtasks for left and right partitions
            ParallelQuickSort leftTask = new ParallelQuickSort(array, low, pivotIndex - 1);
            ParallelQuickSort rightTask = new ParallelQuickSort(array, pivotIndex + 1, high);

            // Fork both tasks to run in parallel
            leftTask.fork();
            rightTask.compute();

            // Wait for left task to complete
            leftTask.join();
        }
    }

    /**
     * Partitions the array around a pivot element
     */
    private int partition(int[] arr, int low, int high) {
        // Choose last element as pivot
        int pivot = arr[high];
        int i = low - 1; // Index of smaller element

        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) {
                i++;
                swap(arr, i, j);
            }
        }

        swap(arr, i + 1, high);
        return i + 1;
    }

    /**
     * Sequential quicksort for small subarrays
     */
    private void sequentialQuickSort(int[] arr, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(arr, low, high);
            sequentialQuickSort(arr, low, pivotIndex - 1);
            sequentialQuickSort(arr, pivotIndex + 1, high);
        }
    }

    /**
     * Swaps two elements in the array
     */
    private void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    /**
     * Public method to sort an array using parallel quicksort
     */
    public static void sort(int[] array) {
        ForkJoinPool pool = new ForkJoinPool();
        try {
            pool.invoke(new ParallelQuickSort(array, 0, array.length - 1));
        } finally {
            pool.shutdown();
        }
    }

    /**
     * Demo and testing
     */
    public static void main(String[] args) {
        // Test with different array sizes
        int[] sizes = {1000, 10000, 100000, 1000000};

        for (int size : sizes) {
            System.out.println("Testing with array size: " + size);

            // Generate random array
            int[] originalArray = generateRandomArray(size);

            // Test parallel quicksort
            int[] parallelArray = Arrays.copyOf(originalArray, originalArray.length);
            long startTime = System.nanoTime();
            ParallelQuickSort.sort(parallelArray);
            long parallelTime = System.nanoTime() - startTime;

            // Test Arrays.sort() for comparison
            int[] javaArray = Arrays.copyOf(originalArray, originalArray.length);
            startTime = System.nanoTime();
            Arrays.sort(javaArray);
            long javaTime = System.nanoTime() - startTime;

            // Verify correctness
            boolean isCorrect = Arrays.equals(parallelArray, javaArray);

            System.out.printf("  Parallel QuickSort: %.2f ms\n", parallelTime / 1_000_000.0);
            System.out.printf("  Arrays.sort():      %.2f ms\n", javaTime / 1_000_000.0);
            System.out.printf("  Speedup:           %.2fx\n", (double) javaTime / parallelTime);
            System.out.printf("  Correct:           %s\n", isCorrect);
            System.out.println();
        }

        // Demonstrate with a small array
        System.out.println("Small array demonstration:");
        int[] smallArray = {64, 34, 25, 12, 22, 11, 90, 5, 77, 30};
        System.out.println("Original: " + Arrays.toString(smallArray));

        ParallelQuickSort.sort(smallArray);
        System.out.println("Sorted:   " + Arrays.toString(smallArray));
    }

    /**
     * Generates a random array of specified size
     */
    private static int[] generateRandomArray(int size) {
        Random random = new Random(42); // Fixed seed for reproducible results
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(size * 10);
        }
        return array;
    }
}

