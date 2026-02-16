package com.example.demo.apps;

import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import static java.lang.IO.println;

public class App39 {
    void main() {

    }

    @SneakyThrows
    void streamProcessing() {
        int[] input = {-1, -2, 2};
        List<Integer> output = processArray(input);
        println(output);
        try (var lines = Files.lines(Path.of("input.txt"))) {
            List<Integer> result =
                    processStream(lines.mapToInt(Integer::parseInt));
        }
    }

    public List<Integer> processArray(int[] input) {
        boolean[] skip = new boolean[input.length];
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < input.length; i++) {
            if (input[i] > 0) {
                skip[i] = true;
            }
        }
        for (int i = 0; i < input.length; i++) {
            if (input[i] < 0 && !skip[i]) {
                result.add(input[i]);
            }
        }
        return result;
    }

    public static List<Integer> processStream(IntStream stream) {
        List<Integer> result = new ArrayList<>();

        stream.forEach(value -> {
            if (value < 0) {
                result.add(value);
            } else if (value > 0) {
                int index = value - 1;
                if (index < result.size()) {
                    result.remove(index);
                }
            }
        });

        return result;
    }

    @SuppressWarnings("unused")
    public static List<Integer> processArrayStream(int[] input) {
        return IntStream.of(input)
                .collect(
                        ArrayList::new,      // supplier
                        App39::accumulate, // accumulator
                        List::addAll         // combiner
                );
    }

    private static void accumulate(List<Integer> result, int value) {
        if (value < 0) {
            result.add(value);
        } else if (value > 0) {
            int index = value - 1;
            if (index < result.size()) {
                result.remove(index);
            }
        }
        // value == 0 → do nothing
    }

    private static final int BATCH_SIZE = 100_000;

    @SuppressWarnings("unused")
    public static List<Integer> processArrayBatch(int[] input) throws Exception {
        int batches = (input.length + BATCH_SIZE - 1) / BATCH_SIZE;

        List<Integer> result = new ArrayList<>();
        try (ExecutorService executor = Executors.newWorkStealingPool()) {
            List<Future<List<Integer>>> futures = new ArrayList<>();

            for (int i = 0; i < batches; i++) {
                int start = i * BATCH_SIZE;
                int end = Math.min(start + BATCH_SIZE, input.length);

                futures.add(executor.submit(() -> processBatch(input, start, end)));
            }

            // Sequential merge
            for (Future<List<Integer>> future : futures) {
                merge(result, future.get());
            }

            executor.shutdown();
        }
        return result;
    }

    private static List<Integer> processBatch(int[] input, int start, int end) {
        List<Integer> result = new ArrayList<>();
        for (int i = start; i < end; i++) {
            int value = input[i];
            if (value < 0) {
                result.add(value);
            } else if (value > 0) {
                int index = value - 1;
                if (index < result.size()) {
                    result.remove(index);
                }
            }
        }
        return result;
    }

    private static void merge(List<Integer> base, List<Integer> batch) {
        for (int value : batch) {
            if (value < 0) {
                base.add(value);
            } else if (value > 0) {
                int index = value - 1;
                if (index < base.size()) {
                    base.remove(index);
                }
            }
        }
    }
}

@SuppressWarnings("unused")
class FenwickProcessor {
    public static List<Integer> processArray(int[] input) {
        int n = input.length;

        int[] values = new int[n];
        FenwickTree tree = new FenwickTree(n);

        int size = 0;

        for (int value : input) {
            if (value < 0) {
                values[size] = value;
                tree.update(size + 1, 1);
                size++;
            } else if (value > 0) {
                if (value <= tree.query(size)) {
                    int pos = tree.findByOrder(value);
                    tree.update(pos, -1);
                }
            }
        }

        // Collect results
        List<Integer> result = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            if (tree.query(i) - tree.query(i - 1) == 1) {
                result.add(values[i - 1]);
            }
        }
        return result;
    }

    static class FenwickTree {
        int[] tree;
        int n;

        FenwickTree(int n) {
            this.n = n;
            tree = new int[n + 1];
        }

        void update(int i, int delta) {
            while (i <= n) {
                tree[i] += delta;
                i += i & -i;
            }
        }

        int query(int i) {
            int sum = 0;
            while (i > 0) {
                sum += tree[i];
                i -= i & -i;
            }
            return sum;
        }

        int findByOrder(int order) {
            int idx = 0;
            int bitMask = Integer.highestOneBit(n);

            for (; bitMask != 0; bitMask >>= 1) {
                int t = idx + bitMask;
                if (t <= n && tree[t] < order) {
                    idx = t;
                    order -= tree[t];
                }
            }
            return idx + 1;
        }
    }
}
