package com.example.demo.apps;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.lang.IO.println;

public class App32 {
    void main() {
        String isViolated = validateOrder(generateOrder());
        println(isViolated);
    }

    private static String validateOrder(List<Product> order) {
        Map<String, Integer> result = new HashMap<>() {{
            put("limited", 0);
            put("unlimited", 0);
            put("isEmbargo", 0);
        }};
        for (int i = 0; i < order.size(); ++i) {
            Product product = order.get(i);
            if (embargoCountris.contains(product.getSourceCountry())) {
                return "VIOLATION";
            }
            if (unlimitedTypes.contains(product.getOilType())) {
                result.put("unlimited", result.get("unlimited") + product.getVolume());
            } else {
                result.put("limited", result.get("limited") + product.getVolume());
            }
        }
        if (result.get("limited") > 100_000) {
            return "VIOLATION";
        }
        if (result.get("unlimited") > 200_000) {
            return "VIOLATION";
        }
        return "COMPLAINT";
    }

    private static List<String> unlimitedTypes = List.of("Crude Oil", "Heavy Crude", "Light Crude");
    private static List<String> embargoCountris = List.of("China");

    private static List<Product> generateOrder() {
        List<Product> order = new LinkedList<>();
        order = List.of(
                new Product(101, "Crude Oil", 250000, "Norway"),
                new Product(102, "Heavy Crude", 5000, "Canada"),
                new Product(103, "Light Crude", 75000, "USA"),
                new Product(104, "Refined Gasoline", 15000, "UAE"),
                new Product(104, "Refined Gasoline", 15000, "USA")
        );
        return order;
    }
}

@Data
@AllArgsConstructor
class Product {
    private Integer productId;
    private String oilType;
    private Integer volume;
    private String sourceCountry;
}

/*
* Product-1: productId=101, oilType="Crude Oil", volume=50000, sourceCountry="Norway"
Product-2: productId=102, oilType="Heavy Crude", volume=25000, sourceCountry="Canada"
Product-3: productId=103, oilType="Light Crude", volume=75000, sourceCountry="USA"
Product-4: productId=104, oilType="Refined Gasoline", volume=15000, sourceCountry="UAE"
* */