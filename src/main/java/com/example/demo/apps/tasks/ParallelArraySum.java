package com.example.demo.apps.tasks;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ParallelArraySum extends RecursiveTask<Long> {
    private static final int THRESHOLD = 10_000;
    private final long[] array;
    private final int start;
    private final int end;

    public ParallelArraySum(long[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        int length = end - start;
        if (length <= THRESHOLD) {
            long sum = 0;
            for (int i = start; i < end; i++) {
                sum += array[i];
            }
            return sum;
        } else {
            int mid = start + length / 2;
            ParallelArraySum leftTask = new ParallelArraySum(array, start, mid);
            ParallelArraySum rightTask = new ParallelArraySum(array, mid, end);
            leftTask.fork();
            long rightResult = rightTask.compute();
            long leftResult = leftTask.join();
            return leftResult + rightResult;
        }
    }

    public static void main(String[] args) {
        int size = 10_000_000;
        long[] array = new long[size];
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(100);
        }
        try (ForkJoinPool pool = new ForkJoinPool()) {
            ParallelArraySum task = new ParallelArraySum(array, 0, array.length);
            long startTime = System.currentTimeMillis();
            long sum = pool.invoke(task);
            long endTime = System.currentTimeMillis();
            System.out.println("Sum: " + sum);
            System.out.println("Time taken: " + (endTime - startTime) + " ms");
        }
    }
}
