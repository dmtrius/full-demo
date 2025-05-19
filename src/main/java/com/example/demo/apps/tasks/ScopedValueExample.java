package com.example.demo.apps.tasks;

public class ScopedValueExample {
    // Define a ScopedValue (Java 21+)
    private static final ScopedValue<String> CURRENT_USER = ScopedValue.newInstance();
    public static void main(String[] args) {
        // Run code with a specific value bound to the ScopedValue
        ScopedValue.where(CURRENT_USER, "Alice").run(() -> {
            processRequest();

            // Value remains available in downstream methods
            auditAction("data_access");
        });
        // Multiple bindings in nested scopes
        ScopedValue.where(CURRENT_USER, "Bob").run(() -> {
            System.out.println("Outer scope: " + CURRENT_USER.get());

            // Can rebind in an inner scope
            ScopedValue.where(CURRENT_USER, "Charlie").run(() -> {
                System.out.println("Inner scope: " + CURRENT_USER.get());
            });

            // Outer binding is preserved
            System.out.println("Back to outer: " + CURRENT_USER.get());
        });
    }
    private static void processRequest() {
        // Access the ScopedValue from another method
        System.out.println("Processing request for: " + CURRENT_USER.get());
    }
    private static void auditAction(String action) {
        // Get user from ScopedValue without having to pass it as a parameter
        System.out.println("User " + CURRENT_USER.get() + " performed action: " + action);
    }
}
