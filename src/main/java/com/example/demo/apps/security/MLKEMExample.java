package com.example.demo.apps.security;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

import static java.lang.IO.println;

public class MLKEMExample {
    void main() throws Exception {
        // Generate a key pair using ML-KEM
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ML-KEM");
//        keyPairGenerator.initialize(1024, new SecureRandom());

        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        println("Public Key: " + keyPair.getPublic());
        println("Private Key: " + keyPair.getPrivate());
    }
}
