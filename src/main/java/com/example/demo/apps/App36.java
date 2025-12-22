package com.example.demo.apps;

import lombok.SneakyThrows;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.IO.println;

public class App36 {
    void main() {
//        m1();
//        m2();
//        println(System.identityHashCode(this));
//        m3();
        m4();
        
    }

    private static void m4() {
        List<Integer> nums = List.of(1, 2, 3, 4, 5, 8);
        int sum = nums.stream().reduce(0, Integer::sum);
        println(sum);
    }

    private static final AtomicInteger counter = new AtomicInteger(0);

    @SneakyThrows
    private static void m3() {
        Runnable r = () -> {
            for (int i = 0; i < 100; ++i) {
                counter.incrementAndGet();
            }
        };
        Thread tt1 = new Thread(r);
        Thread tt2 = new Thread(r);
        tt1.start();
        tt2.start();
        tt1.join();
        tt2.join();
        println(counter.get());
        counter.set(0);
    }

    @SneakyThrows
    private static void m2() {
        Thread t1 = new Thread(() -> println(Thread.currentThread().getName()));
        t1.start();
        t1.join();
        Thread.yield();
        println("MT");
    }

    private static void m1() {
        String in = """
                Nobilis, rusticus byssuss sapienter imitari de neuter, audax canis.
                Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.
                Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi.
                Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis.
                Nam liber tempor cum soluta nobis eleifend option congue nihil imperdiet doming id quod mazim placerat facer possim assum. Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam n
                """;
        println(countDuplicateCharacters(in));
        println(reverseWords(in));
    }

    private static Map<String, Long> countDuplicateCharacters(String str) {
        return str.chars()
                .filter(c -> c >= ' ')
                .mapToObj(c -> (char) c)
                .collect(Collectors
                        .groupingBy(c -> "'" + c + "'", Collectors.counting()));
    }

    private static final Pattern PATTERN = Pattern.compile(" +");

    public static String reverseWords(String str) {
        return PATTERN.splitAsStream(str)
                .map(w -> new StringBuilder(w).reverse())
                .collect(Collectors.joining(" "));
    }
}
