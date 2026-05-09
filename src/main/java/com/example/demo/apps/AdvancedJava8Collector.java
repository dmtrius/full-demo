package com.example.demo.apps;

import com.github.javafaker.Faker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static java.lang.IO.println;

public class AdvancedJava8Collector {

    record Order(String id, String customer, double amount, String product, Date date) {
    }

    record Analytics(double totalRevenue, double avgOrderValue,
                     String topProduct, Map<String, Double> monthlyRevenue) {
    }

    void main() {
        var orders = getOrders(10);
        var result = orders.parallelStream()
            .collect(new OrderAnalyticsCollector());
        println(result);
        orders = getOrders(20);
        result = orders.parallelStream()
            .collect(new OrderAnalyticsCollector());
        println(result);
    }

    private static List<Order> getOrders(int n) {
        List<Order> result = new ArrayList<>(n);
        Faker faker = new Faker();
        for (int i = 0; i < n; ++i) {
            double price = BigDecimal.valueOf(faker.random().nextDouble() * 1000)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
            result.add(
                new Order(
                    faker.idNumber().valid(),
                    faker.name().fullName(),
                    price,
                    faker.commerce().productName(),
                    faker.date().past(365, java.util.concurrent.TimeUnit.DAYS)
                )
            );
        }
        return result;
    }

    static class OrderAnalyticsCollector implements Collector<Order, OrderAnalyticsCollector.Acc, Analytics> {

        protected static class Acc {
            double revenue = 0;
            int count = 0;
            Map<String, Long> productCount = new HashMap<>();
            Map<String, Double> monthly = new HashMap<>();
        }

        @Override
        public Supplier<Acc> supplier() {
            return Acc::new;
        }

        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

        @Override
        public BiConsumer<Acc, Order> accumulator() {
            return (acc, o) -> {
                acc.revenue += o.amount();
                acc.count++;
                acc.productCount.merge(o.product(), 1L, Long::sum);
                String month = o.date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(FORMATTER);
                acc.monthly.merge(month, o.amount(), Double::sum);
            };
        }

        @Override
        public BinaryOperator<Acc> combiner() {
            return (a1, a2) -> {
                a1.revenue += a2.revenue;
                a1.count += a2.count;
                a2.productCount.forEach((k, v) -> a1.productCount.merge(k, v, Long::sum));
                a2.monthly.forEach((k, v) -> a1.monthly.merge(k, v, Double::sum));
                return a1;
            };
        }

        @Override
        public Function<Acc, Analytics> finisher() {
            return acc -> new Analytics(
                BigDecimal.valueOf(acc.revenue / acc.count)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue(),
                BigDecimal.valueOf(acc.revenue)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue(),
                acc.productCount.entrySet().stream()
                    .max(Map.Entry.comparingByValue()).map(Map.Entry::getKey)
                    .orElse("N/A"),
                acc.monthly
            );
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Set.of(Characteristics.CONCURRENT, Characteristics.UNORDERED);
        }
    }
}
