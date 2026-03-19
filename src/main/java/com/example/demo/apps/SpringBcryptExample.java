package com.example.demo.apps;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static java.lang.IO.println;

public class SpringBcryptExample {

    void main() {
        println("--- BCrypt Example ---");
        bCryptExample();
        println("\n--- Argon2 Example ---");
        argonExample();
    }

    void bCryptExample() {
        // Create an instance of BCryptPasswordEncoder
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        // The plain text password to be hashed
        String plainPassword = "mySecurePassword";
        // Hash the password
        String hashedPassword = passwordEncoder.encode(plainPassword);
        println("Hashed Password: " + hashedPassword);
        // Verify the password
        boolean isPasswordCorrect = passwordEncoder.matches(plainPassword, hashedPassword);
        println("Is Password Correct? " + isPasswordCorrect);
    }

    void argonExample() {
        // Create an instance of Argon2PasswordEncoder
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder(16, 32, 1, 65536, 3);
        // The plain text password to be hashed
        String plainPassword = "mySecurePassword";
        // Hash the password
        String hashedPassword = passwordEncoder.encode(plainPassword);
        println("Hashed Password: " + hashedPassword);
        // Verify the password
        boolean isPasswordCorrect = passwordEncoder.matches(plainPassword, hashedPassword);
        println("Is Password Correct? " + isPasswordCorrect);
    }
}
