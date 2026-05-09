package com.example.demo.apps;

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
        List<Order> orders = getOrders();
        Analytics result = orders.parallelStream()
            .collect(new OrderAnalyticsCollector());
        println(result);
    }

    private static List<Order> getOrders() {
        return List.of(
            new Order("1", "Alice", 100.0, "Widget", new Date()),
            new Order("2", "Bob", 150.0, "Gadget", new Date()),
            new Order("3", "Charlie", 200.0, "Widget", new Date())
        );
    }

    static class OrderAnalyticsCollector
        implements Collector<Order, OrderAnalyticsCollector.Acc, Analytics> {

        static class Acc {
            double revenue = 0;
            int count = 0;
            Map<String, Long> productCount = new HashMap<>();
            Map<String, Double> monthly = new HashMap<>();
        }

        @Override
        public Supplier<Acc> supplier() {
            return Acc::new;
        }

        @Override
        public BiConsumer<Acc, Order> accumulator() {
            return (acc, o) -> {
                acc.revenue += o.amount();
                acc.count++;
                acc.productCount.merge(o.product(), 1L, Long::sum);

                String month = new java.text.SimpleDateFormat("yyyy-MM")
                    .format(o.date());
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
                acc.revenue / acc.count,
                acc.revenue,
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
