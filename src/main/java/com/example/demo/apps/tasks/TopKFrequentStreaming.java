package com.example.demo.apps.tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import static java.lang.IO.println;

public class TopKFrequentStreaming {
    record Element<T>(T value, int frequency) {
    }

    public static List<Integer> topKFrequent(Iterator<Integer> stream, int k) {
        Map<Integer, Integer> freqMap = new HashMap<>();

        // Step 1: Process stream
        while (stream.hasNext()) {
            int num = stream.next();
            freqMap.put(num, freqMap.getOrDefault(num, 0) + 1);
        }

        // Step 2: Min-Heap based on frequency
        PriorityQueue<Element<Integer>> minHeap = new PriorityQueue<>(
                Comparator.comparingInt(e -> e.frequency)
        );

        // Step 3: Maintain top K
        for (var entry : freqMap.entrySet()) {
            minHeap.offer(new Element<>(entry.getKey(), entry.getValue()));

            if (minHeap.size() > k) {
                minHeap.poll(); // remove smallest frequency
            }
        }

        // Step 4: Extract results
        List<Integer> result = new ArrayList<>();
        while (!minHeap.isEmpty()) {
            result.add(minHeap.poll().value);
        }

        Collections.reverse(result); // highest frequency first
        return result;
    }

    Map<Integer, Integer> misraGries(Iterator<Integer> stream, int k) {
        Map<Integer, Integer> counters = new HashMap<>();

        while (stream.hasNext()) {
            int num = stream.next();

            if (counters.containsKey(num)) {
                counters.put(num, counters.get(num) + 1);
            } else if (counters.size() < k - 1) {
                counters.put(num, 1);
            } else {
                Iterator<Map.Entry<Integer, Integer>> it = counters.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<Integer, Integer> entry = it.next();
                    int count = entry.getValue() - 1;
                    if (count == 0) {
                        it.remove();
                    } else {
                        entry.setValue(count);
                    }
                }
            }
        }

        return counters;
    }

    void main() {
        List<Integer> data = Arrays.asList(1, 1, 1, 2, 2, 3, 3, 3, 3, 4, 5, 5, 5, 5, 5);
        List<Integer> topK = topKFrequent(data.iterator(), 10);

        println(topK);

        var topKMap = misraGries(data.iterator(), 10);
        println(topKMap);
    }
}
