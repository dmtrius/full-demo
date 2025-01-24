package com.example.demo.apps;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class SpringBcryptExample {

    public static void main(String[] args) {
        // Create an instance of BCryptPasswordEncoder
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // The plain text password to be hashed
        String plainPassword = "mySecurePassword";

        // Hash the password
        String hashedPassword = passwordEncoder.encode(plainPassword);
        System.out.println("Hashed Password: " + hashedPassword);

        // Verify the password
        boolean isPasswordCorrect = passwordEncoder.matches(plainPassword, hashedPassword);
        System.out.println("Is Password Correct? " + isPasswordCorrect);
    }
}
