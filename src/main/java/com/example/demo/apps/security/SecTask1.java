package com.example.demo.apps.security;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

@Slf4j
public class SecTask1 {
    void main() throws NoSuchAlgorithmException {
        main1();
        main2();
        main3();
        main4();
    }

    private static final Random rand = new SecureRandom();

    @SuppressWarnings({"java:S5542", "java:S5547"})
    void main4() {
        try {
            // List all available providers and their services
            for (Provider provider : Security.getProviders()) {
                IO.println("Provider: " + provider.getName());
                for (Provider.Service service : provider.getServices()) {
                    if (service.getType().equals("KeyGenerator")) {
                        IO.println("  Algorithm: " + service.getAlgorithm());
                    }
                }
            }

            // Generate an AES key with default provider
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256); // Initialize with keysize
            SecretKey secretKey = keyGen.generateKey();
            IO.println("Generated AES key: " + secretKey.getAlgorithm());

            // Generate an HMAC-SHA256 key with specific provider
            keyGen = KeyGenerator.getInstance("HmacSHA256", "SunJCE");
            secretKey = keyGen.generateKey();
            IO.println("Generated HMAC-SHA256 key: " + secretKey.getAlgorithm());

            keyGen = KeyGenerator.getInstance("Blowfish", "SunJCE");
            keyGen.init(448);
            secretKey = keyGen.generateKey();
            IO.println("Generated Blowfish key: " + secretKey.getAlgorithm());
            IO.println(Arrays.toString(secretKey.getEncoded()));
            // Generate IV
            byte[] iv = new byte[8]; // 64-bit IV for Blowfish
            rand.nextBytes(iv);
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
            IO.println("Decrypted: " + new String(decodedBytes));
        } catch (GeneralSecurityException e) {
            log.error("Security exception occurred: ", e);
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
        IO.println("Available KeyGenerator algorithms:");
        algorithms.forEach(IO::println);
    }

    void main2() throws NoSuchAlgorithmException {
        byte[] keyBytes = Hex.decode("000102030405060708090a0b0c0d0e0f");
        IO.println(Arrays.toString(keyBytes));

        KeyGenerator kGen = KeyGenerator.getInstance("HmacSHA512");
        SecretKey key = kGen.generateKey();
        IO.println(Arrays.toString(key.getEncoded()));
    }

    void main1() {
        Arrays.stream(Security.getProviders())
            .forEach(provider -> {
                IO.println("%s :: %s%n".formatted(provider.getName(), provider.getInfo()));
                provider.forEach((key, _) -> IO.println("  %s >> %s%n".formatted(key, provider.get(key))));
            });
    }
}
