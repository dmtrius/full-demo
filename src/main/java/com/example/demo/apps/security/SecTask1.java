package com.example.demo.apps.security;

import org.bouncycastle.util.encoders.Hex;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;
import java.util.Base64;
import java.util.Set;
import java.util.TreeSet;

public class SecTask1 {
    void main() throws Exception {
//        main1();
//        main2();
//        main3();
        main4();
    }

    void main4() {
        try {
            // List all available providers and their services
            for (Provider provider : Security.getProviders()) {
                System.out.println("Provider: " + provider.getName());
                for (Provider.Service service : provider.getServices()) {
                    if (service.getType().equals("KeyGenerator")) {
                        System.out.println("  Algorithm: " + service.getAlgorithm());
                    }
                }
            }

            // Generate an AES key with default provider
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256); // Initialize with keysize
            SecretKey secretKey = keyGen.generateKey();
            System.out.println("Generated AES key: " + secretKey.getAlgorithm());

            // Generate an HMAC-SHA256 key with specific provider
            keyGen = KeyGenerator.getInstance("HmacSHA256", "SunJCE");
            secretKey = keyGen.generateKey();
            System.out.println("Generated HMAC-SHA256 key: " + secretKey.getAlgorithm());

            keyGen = KeyGenerator.getInstance("Blowfish", "SunJCE");
            keyGen.init(448);
            secretKey = keyGen.generateKey();
            System.out.println("Generated Blowfish key: " + secretKey.getAlgorithm());
            System.out.println(Arrays.toString(secretKey.getEncoded()));
            // Generate IV
            byte[] iv = new byte[8]; // 64-bit IV for Blowfish
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // Initialize cipher for encryption
            Cipher cipher = Cipher.getInstance("Blowfish/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            // Encrypt
            String plaintext = "Secret message: 2025-05-18T21:28:46.967+02:00";
            String encodedString = Base64.getEncoder().encodeToString(plaintext.getBytes());
            byte[] encrypted = cipher.doFinal(encodedString.getBytes());

            // Initialize cipher for decryption
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            // Decrypt
            byte[] decrypted = cipher.doFinal(encrypted);
            byte[] decodedBytes = Base64.getDecoder().decode(decrypted);
            System.out.println("Decrypted: " + new String(decodedBytes));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    void main3() {
        Set<String> algorithms = new TreeSet<>();
        for (Provider provider : Security.getProviders()) {
            for (Provider.Service service : provider.getServices()) {
                if (service.getType().equals("KeyGenerator")) {
                    algorithms.add(service.getAlgorithm());
                }
            }
        }
        System.out.println("Available KeyGenerator algorithms:");
        algorithms.forEach(System.out::println);
    }

    void main2() throws NoSuchAlgorithmException, NoSuchProviderException {
        byte[] keyBytes = Hex.decode("000102030405060708090a0b0c0d0e0f");
        System.out.println(Arrays.toString(keyBytes));

        KeyGenerator kGen = KeyGenerator.getInstance("HmacSHA512");
        SecretKey key = kGen.generateKey();
        System.out.println(Arrays.toString(key.getEncoded()));
    }

    void main1() {
        Provider[] providers = Security.getProviders();
        Arrays.stream(providers)
                .forEach(p -> {
                    System.out.print(p.getName());
                    System.out.print(" :: ");
                    System.out.println(p.getInfo());
                    p.forEach((key, value) ->
                            System.out.println(key + " >> " + value));
                });
    }
}
