package com.example.demo.apps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestrictedRulesEngine {
    private static final int BULK_BUY_LIMIT = 10;
    private static final int BULK_BUY_LIMIT_CATEGORY = 5;
    private static final String BREACHED = "BREACHED";
    private static final String MET = "MET";
    private static final String RESTRICTED_CATEGORY = "Paracetamol";

    void main() {
        List<Product> shoppingCart = List.of(
                new Product(1, RESTRICTED_CATEGORY, 3),
                new Product(2, "analgesic", 3),
                new Product(3, "chocolate", 8),
                new Product(4, RESTRICTED_CATEGORY, 2)
        );

        String status = checkRestrictions(shoppingCart);
        System.out.println("Restriction Status: " + status);
    }

    public static String checkRestrictions(List<Product> shoppingCart) {
        Map<String, Integer> categoryQuantityMap = new HashMap<>();

        for (Product product : shoppingCart) {
            categoryQuantityMap.put(product.category,
                    categoryQuantityMap.getOrDefault(product.category, 0)
                            + product.quantity);

            // Check the bulk buy limit for any product
            if (product.quantity > BULK_BUY_LIMIT) {
                return BREACHED;
            }

            // Check bulk buy limit for Paracetamol category
            if (product.category.equalsIgnoreCase(RESTRICTED_CATEGORY) &&
                    categoryQuantityMap.getOrDefault(
                            RESTRICTED_CATEGORY, 0) > BULK_BUY_LIMIT_CATEGORY) {
                return BREACHED;
            }
        }

        return MET;
    }

    record Product(int productId, String category, int quantity){}
}
