package com.example.demo.apps;

import com.example.demo.apps.tasks.TimeTracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App25 {

    void main() {
        //TimeTracker.track("Operation >> ", this::main8);
        TimeTracker.track("...Ops...", () -> {
            main1();
            main2();
            main3();
            main4();
            main5();
            main6();
            main7();
            TimeUnit.SECONDS.sleep(2);
        });
    }

    void main8() {
        Stream<RandomGeneratorFactory<RandomGenerator>> allPRNG = RandomGeneratorFactory.all();
        allPRNG.map(RandomGeneratorFactory::name)
                .sorted().forEach(System.out::println);
    }

    void main7() {
        int[] arr = {-2, 1, -3, 4, -1, 2, 1, -5, 4};
        int maxSum = findLargestSubarraySum(arr);
        System.out.println("The largest subarray sum is: " + maxSum);
    }

    public static int findLargestSubarraySum(int[] arr) {
        int maxSoFar = arr[0];
        int maxEndingHere = arr[0];

        for (int i = 1; i < arr.length; i++) {
            maxEndingHere = Math.max(arr[i], maxEndingHere + arr[i]);
            maxSoFar = Math.max(maxSoFar, maxEndingHere);
        }

        return maxSoFar;
    }

    void main6() {
        int[] arr = {1, 3, 8, 12, 4, 2};
        int peak = findPeakElement(arr);
        System.out.println("The peak element is: " + peak);
    }

    public static int findPeakElement(int[] arr) {
        int left = 0;
        int right = arr.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (arr[mid] < arr[mid + 1]) {
                // Peak is in the right half
                left = mid + 1;
            } else {
                // Peak is in the left half or at mid
                right = mid;
            }
        }

        // left and right will converge to the peak element
        return arr[left];
    }

    void main5() {
        List<Integer> numbers = List.of(1, 2, 3, 4);
        String result = numbers.stream()
                .collect(Collectors.teeing(
                        Collectors.summingInt(Integer::intValue),
                        Collectors.counting(),
                        (sum, count) -> "Sum: " + sum + ", Count: " + count
                ));
        System.out.println(result);
    }

    void main4() {
        Collector<String, ?, String> joinWithPrefix = Collector.of(
                StringBuilder::new,
                (sb, s) -> sb.append("prefix-").append(s).append(" | "),
                StringBuilder::append,
                StringBuilder::toString,
                Collector.Characteristics.CONCURRENT
        );
        List<String> items = List.of("a", "b");
        String result = items.stream().collect(joinWithPrefix);
        System.out.println(result);
    }

    void main3() {
        List<Integer> numbers = List.of(1, 2, 3, 4);
        Map<Boolean, List<Integer>> partitioned = numbers.stream()
                .collect(Collectors.partitioningBy(n -> n % 2 == 0));
        System.out.println(partitioned);
    }

    void main2() {
        List<String> words = List.of("apple", "banana", "apricot");
        Map<Character, List<String>> grouped = words.stream()
                .collect(Collectors.groupingBy(word -> word.charAt(0)));
        System.out.println(grouped);
    }

    void main1() {
        int[] arr = new int[]{1,2,3};
        Arrays.stream(arr).boxed().flatMap(Stream::ofNullable)
                .forEach(System.out::println);
    }
}
