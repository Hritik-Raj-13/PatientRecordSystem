package com.hr.patient_record_application.utilities;

import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class RSAKeyGenerator {

    private static final int KEY_SIZE = 2048;  // RSA key size

    public static void main(String[] args) {
        try {
            // Initialize KeyPairGenerator for RSA
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(KEY_SIZE);

            // Generate the key pair
            KeyPair keyPair = keyGen.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            // Save the public and private keys to files
            saveKeyToFile("src/main/resources/keys/publicKey.pem", publicKey.getEncoded(), "PUBLIC");
            saveKeyToFile("src/main/resources/keys/privateKey.pem", privateKey.getEncoded(), "PRIVATE");

            System.out.println("RSA Key Pair generated and saved to files.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveKeyToFile(String filename, byte[] key, String keyType) throws Exception {
        // Encode to base64 for easy file saving
        String base64Key = Base64.getEncoder().encodeToString(key);

        // Prepare PEM format based on key type
        StringBuilder pemFormat = new StringBuilder();
        if ("PUBLIC".equals(keyType)) {
            pemFormat.append("-----BEGIN PUBLIC KEY-----\n");
        } else if ("PRIVATE".equals(keyType)) {
            pemFormat.append("-----BEGIN PRIVATE KEY-----\n");
        }
        pemFormat.append(base64Key.replaceAll("(.{64})", "$1\n")); // Break lines every 64 characters
        if ("PUBLIC".equals(keyType)) {
            pemFormat.append("-----END PUBLIC KEY-----\n");
        } else if ("PRIVATE".equals(keyType)) {
            pemFormat.append("-----END PRIVATE KEY-----\n");
        }

        // Write to file
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(pemFormat.toString().getBytes());
            fos.flush();
        }
    }
}
