package com.example.demo.apps;

import lombok.extern.slf4j.Slf4j;
import org.joou.ULong;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

import static java.lang.IO.println;
import static org.joou.Unsigned.ulong;

@Slf4j
public class App37 {
    void main() {
        m1();
        println(factors(90));
        println(Math.sqrt(65d));
        println(squareRootBabylonian(139));
        println(squareRootBabylonian(65));
        println(Math.TAU);
        m2();
        println((LocalDate.of(2000, 1, 1).getYear() - 1) / 100 + 1);
        println((LocalDate.of(2001, 1, 1).getYear() - 1) / 100 + 1);
        m3(List.of("1"));

    }

    void m3(List<String> s, Void... v) {
        println(s);
        println(Arrays.toString(v));
    }

    /**
     * A telemeter with laser ranging from 0 to 60 ft including
     * calculation of surfaces and volumes with high-precision
     *
     * <pre>{@code
     * Telemeter.Calibrate.at(0.00001); // @highlight region
     * Telemeter telemeter = new Telemeter(0.15, 2, "IP54"); // @end
     * }</pre>
     *
     * <pre>{@snippet lang = "properties":
     *     telemeter.precision.default=42
     *     telemeter.clazz.default=2
     * }</pre>
     */
    @SuppressWarnings("unused")
    static void m2() {
        RandomGenerator.SplittableGenerator splittableRndL64X256
                = RandomGeneratorFactory
                .<RandomGenerator.SplittableGenerator>of("L64X256MixRandom")
                .create();
        long[] arr = new long[100_000_000];
        Arrays.parallelSetAll(arr,
                x -> splittableRndL64X256.nextLong());
        Arrays.stream(arr)
                .limit(20)
                .forEach(n -> log.info(Long.toString(n)));
    }

    static void m1() {
        ULong ux = ulong(10L);
        println(ux.floatValue());
    }

    static double squareRootBabylonian(double v) {
        double x = v / 2;
        double y = 1;
        double e = 0.00000000000001; // precision
        while (x - y > e) {
            x = (x + y) / 2;
            y = v / x;
        }
        return x;
    }

    static List<Integer> factors(int v) {
        var factorsList = new ArrayList<Integer>();
        int s = 2;

        while (v > 1) {
            if (v % s == 0) {
                factorsList.add(s);
                v /= s;
            } else {
                ++s;
            }
        }

        return factorsList;
    }
}
