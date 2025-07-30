package com.example.demo.apps.tasks;

import java.util.*;
import java.util.function.BiFunction;

import static java.io.IO.println;

public class FoldUtils {

    // foldLeft: (accumulator, element) -> result
    public static <T, R> R foldLeft(List<T> list, R initial, BiFunction<R, T, R> op) {
        R result = initial;
        for (T element : list) {
            result = op.apply(result, element);
        }
        return result;
    }

    // foldRight: (element, accumulator) -> result
    public static <T, R> R foldRight(List<T> list, R initial, BiFunction<T, R, R> op) {
        R result = initial;
        ListIterator<T> it = list.listIterator(list.size());
        while (it.hasPrevious()) {
            result = op.apply(it.previous(), result);
        }
        return result;
    }

    // fold: same as foldLeft by default
    public static <T, R> R fold(List<T> list, R initial, BiFunction<R, T, R> op) {
        return foldLeft(list, initial, op);
    }

    @SuppressWarnings({"unused", "preview"})
    // Demo
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4);

        // foldLeft: (((0 + 1) + 2) + 3) + 4
        int sumLeft = foldLeft(numbers, 0, Integer::sum);
        println("foldLeft sum: " + sumLeft);  // 10

        // foldRight: 1 + (2 + (3 + (4 + 0)))
        int sumRight = foldRight(numbers, 0, Integer::sum);
        println("foldRight sum: " + sumRight);  // 10

        // fold: defaults to foldLeft
        int sum = fold(numbers, 0, Integer::sum);
        println("fold (default): " + sum);  // 10

        // foldRight with String
        String result = foldRight(Arrays.asList("a", "b", "c"), "", (s, acc) -> s + acc);
        println("foldRight string: " + result);  // "abc"

        // foldLeft with String
        String result2 = foldLeft(Arrays.asList("a", "b", "c"), "", (acc, s) -> acc + s);
        println("foldLeft string: " + result2);  // "abc"
    }
}
