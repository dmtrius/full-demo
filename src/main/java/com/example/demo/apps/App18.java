package com.example.demo.apps;

import lombok.SneakyThrows;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class App18 {
    void main() {
//        testCache();
//        testUnnamedClasses();
        testValidity();
    }

    private static void testValidity() {
        String s1 = "()";
        String s2 = "(*)";
        String s3 = "(*))";
        String s4 = "((*)";
        String s5 = "(*()";

        System.out.println(checkValidString(s1));  // true
        System.out.println(checkValidString(s2));  // true
        System.out.println(checkValidString(s3));  // true
        System.out.println(checkValidString(s4));  // true
        System.out.println(checkValidString(s5));  // true
    }

    public static boolean checkValidString(String s) {
        int leftMin = 0;  // Minimum number of '('
        int leftMax = 0;  // Maximum number of '('

        for (char c : s.toCharArray()) {
            if (c == '(') {
                leftMin++;
                leftMax++;
            } else if (c == ')') {
                leftMin--;
                leftMax--;
            } else if (c == '*') {
                leftMin--;  // Treat '*' as ')'
                leftMax++;  // Treat '*' as '('
            }

            if (leftMax < 0) {
                return false;  // More ')' than '('
            }

            leftMin = Math.max(leftMin, 0);  // Ensure leftMin doesn't go negative
        }

        return leftMin == 0;  // All '(' are matched
    }
    
    private static void testUnnamedClasses() {
        new Object() {
            void startThread() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Thread running inside an unnamed class.");
                    }
                }).start();
            }
        }.startThread();
    }

    @SneakyThrows
    private static void testCache() {
        final int N = 3;
        for (int i = 0; i < N; ++i) {
            var cachedData = fetchData();
            System.out.println("NO CACHE: " + cachedData.block());
            TimeUnit.MILLISECONDS.sleep(1);
        }
        for (int i = 0; i < N; ++i) {
            var cachedData = fetchData().cache();
            System.out.println("CACHED: " + cachedData.block());
            TimeUnit.MILLISECONDS.sleep(100);
        }
        for (int i = 0; i < N; ++i) {
            var cachedDataWithDuration = fetchData().cache(Duration.ofSeconds(1));
            System.out.println("CACHED DURATION: " + cachedDataWithDuration.block());
            TimeUnit.MILLISECONDS.sleep(100);
        }
    }

    private static Mono<String> fetchData() {
        return Mono.fromSupplier(() -> {
            System.out.println("Fetching data from service...");
            return "Data from service";
        }).delayElement(Duration.ofMillis(200));
    }
}
