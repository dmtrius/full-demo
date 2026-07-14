package com.example.demo.apps.security;

import java.util.Scanner;

public class DiffieHellman {
    void main() {
        try (Scanner sc = new Scanner(System.in)) {

            IO.println("Enter modulo (prime number p):");
            int p = sc.nextInt();

            IO.println("Enter primitive root of " + p + " (base g):");
            int g = sc.nextInt();

            IO.println("Enter 1st secret number (private key of Alice):");
            int a = sc.nextInt();

            IO.println("Enter 2nd secret number (private key of Bob):");
            int b = sc.nextInt();

            // Compute public keys
            int computedA = (int) Math.pow(g, a) % p;
            int computedB = (int) Math.pow(g, b) % p;

            // Compute shared secret keys
            int secretKeyAlice = (int) Math.pow(computedB, a) % p;
            int secretKeyBob = (int) Math.pow(computedA, b) % p;

            if (secretKeyAlice == secretKeyBob) {
                IO.println("Shared secret key established: " + secretKeyAlice);
            } else {
                IO.println("Key exchange failed. Secret keys do not match.");
            }

        }
    }
}
