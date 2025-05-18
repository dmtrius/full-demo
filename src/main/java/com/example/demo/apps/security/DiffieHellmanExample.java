package com.example.demo.apps.security;

import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

public class DiffieHellmanExample {

    public static void main(String[] args) throws Exception {
        // Step 1: Generate Alice's key pair
        KeyPairGenerator aliceKpairGen = KeyPairGenerator.getInstance("DH");
        aliceKpairGen.initialize(2048);
        KeyPair aliceKpair = aliceKpairGen.generateKeyPair();

        // Step 2: Alice creates and initializes her KeyAgreement object
        KeyAgreement aliceKeyAgree = KeyAgreement.getInstance("DH");
        aliceKeyAgree.init(aliceKpair.getPrivate());

        // Step 3: Alice sends her public key to Bob (simulate by encoding)
        byte[] alicePubKeyEnc = aliceKpair.getPublic().getEncoded();

        // Step 4: Bob receives Alice's public key and generates his own key pair using the same parameters
        KeyFactory bobKeyFac = KeyFactory.getInstance("DH");
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(alicePubKeyEnc);
        PublicKey alicePubKey = bobKeyFac.generatePublic(x509KeySpec);

        // Extract DH parameters from Alice's public key
        DHParameterSpec dhParamSpec = ((DHPublicKey) alicePubKey).getParams();

        // Bob generates his key pair
        KeyPairGenerator bobKpairGen = KeyPairGenerator.getInstance("DH");
        bobKpairGen.initialize(dhParamSpec);
        KeyPair bobKpair = bobKpairGen.generateKeyPair();

        // Step 5: Bob creates and initializes his KeyAgreement object
        KeyAgreement bobKeyAgree = KeyAgreement.getInstance("DH");
        bobKeyAgree.init(bobKpair.getPrivate());

        // Step 6: Bob sends his public key to Alice (simulate by encoding)
        byte[] bobPubKeyEnc = bobKpair.getPublic().getEncoded();

        // Step 7: Alice receives Bob's public key
        X509EncodedKeySpec x509KeySpec2 = new X509EncodedKeySpec(bobPubKeyEnc);
        PublicKey bobPubKey = bobKeyFac.generatePublic(x509KeySpec2);

        // Step 8: Both parties do the phase with the other's public key
        aliceKeyAgree.doPhase(bobPubKey, true);
        bobKeyAgree.doPhase(alicePubKey, true);

        // Step 9: Generate the shared secret
        byte[] aliceSharedSecret = aliceKeyAgree.generateSecret();
        byte[] bobSharedSecret = bobKeyAgree.generateSecret();

        // Verify both shared secrets are the same
        System.out.println("Alice's secret: " + Arrays.toString(aliceSharedSecret));
        System.out.println("Bob's secret:   " + Arrays.toString(bobSharedSecret));
        System.out.println("Secrets are equal: " + Arrays.equals(aliceSharedSecret, bobSharedSecret));
    }
}
