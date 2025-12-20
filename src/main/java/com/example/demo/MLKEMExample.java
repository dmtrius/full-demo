package com.example.demo;

//import module java.base;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

import static java.lang.IO.println;

public class MLKEMExample {
    public static void main(String[] args) throws Exception {
        // Generate a key pair using ML-KEM
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ML-KEM");
//        keyPairGenerator.initialize(512); // Use ML-KEM-512 parameter set

        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        println("Public Key: " + keyPair.getPublic());
        println("Private Key: " + keyPair.getPrivate());
    }
}
