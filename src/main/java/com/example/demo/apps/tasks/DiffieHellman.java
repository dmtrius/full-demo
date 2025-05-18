package com.example.demo.apps.tasks;

import java.util.Scanner;

public class DiffieHellman {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter modulo (prime number p):");
        int p = sc.nextInt();

        System.out.println("Enter primitive root of " + p + " (base g):");
        int g = sc.nextInt();

        System.out.println("Enter 1st secret number (private key of Alice):");
        int a = sc.nextInt();

        System.out.println("Enter 2nd secret number (private key of Bob):");
        int b = sc.nextInt();

        // Compute public keys
        int A = (int) Math.pow(g, a) % p;
        int B = (int) Math.pow(g, b) % p;

        // Compute shared secret keys
        int secretKeyAlice = (int) Math.pow(B, a) % p;
        int secretKeyBob = (int) Math.pow(A, b) % p;

        if (secretKeyAlice == secretKeyBob) {
            System.out.println("Shared secret key established: " + secretKeyAlice);
        } else {
            System.out.println("Key exchange failed. Secret keys do not match.");
        }

        sc.close();
    }
}
