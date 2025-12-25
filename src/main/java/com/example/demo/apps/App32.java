package com.example.demo.apps;

import org.apache.groovy.util.Maps;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static java.lang.IO.println;

public class App32 {
    void main() {
        String isViolated = validateOrder(generateOrder());
        println(isViolated);
    }

    public static final int LIMITED_VOLUME = 100_000;
    public static final int UNLIMITED_VOLUME = 200_000;

    private static String validateOrder(List<Product> order) {
        var result = initResult();
        for (Product product : order) {
            if (embargoCountries.contains(product.sourceCountry())) {
                return OrderValidity.VIOLATION.name();
            }
            if (unlimitedTypes.contains(product.oilType())) {
                result.merge(ProductType.UNLIMITED, 1, Integer::sum);
            } else {
                result.merge(ProductType.LIMITED, 1, Integer::sum);
            }
        }
        if (result.get(ProductType.LIMITED) > LIMITED_VOLUME) {
            return OrderValidity.VIOLATION.name();
        }
        if (result.get(ProductType.UNLIMITED) > UNLIMITED_VOLUME) {
            return OrderValidity.VIOLATION.name();
        }
        return OrderValidity.COMPLAINT.name();
    }

    private enum OrderValidity {
        VIOLATION,
        COMPLAINT
    }

    private enum ProductType {
        LIMITED,
        UNLIMITED,
        EMBARGO
    }

    private static final List<String> unlimitedTypes = List.of("Crude Oil", "Heavy Crude", "Light Crude");
    private static final List<String> embargoCountries = List.of("China");

    private static Map<ProductType, Integer> initResult() {
        return new EnumMap<>(Maps.of(
                ProductType.LIMITED, 0,
                ProductType.UNLIMITED, 0,
                ProductType.EMBARGO, 0
        ));
    }

    private static List<Product> generateOrder() {
        return List.of(
                new Product(101, "Crude Oil", 250000, "Norway"),
                new Product(102, "Heavy Crude", 5000, "Canada"),
                new Product(103, "Light Crude", 75000, "USA"),
                new Product(104, "Refined Gasoline", 15000, "UAE"),
                new Product(104, "Refined Gasoline", 15000, "USA")
        );
    }
}

record Product(
        Integer productId,
        String oilType,
        Integer volume,
        String sourceCountry
) {
}

/*
* Product-1: productId=101, oilType="Crude Oil", volume=50000, sourceCountry="Norway"
Product-2: productId=102, oilType="Heavy Crude", volume=25000, sourceCountry="Canada"
Product-3: productId=103, oilType="Light Crude", volume=75000, sourceCountry="USA"
Product-4: productId=104, oilType="Refined Gasoline", volume=15000, sourceCountry="UAE"
* */