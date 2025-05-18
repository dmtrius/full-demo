package com.example.demo.apps;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App25 {

    void main() {
        main5();
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
