package com.hr.patient_record_application.utilities;


import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

@Service
public class EncryptionService {
    private static final String AES_ALGORITHM = "AES/GCM/NoPadding";
    private static final int AES_KEY_SIZE = 256;
    private static final int GCM_TAG_LENGTH = 128;

    // Load the RSA public key from file
    private PublicKey loadRSAPublicKey() throws Exception {
        // Read all bytes from the public key file
        String key = new String(Files.readAllBytes(Paths.get("src/main/resources/keys/publicKey.pem")));

        // Remove the header and footer
        String publicKeyPEM = key.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", ""); // Remove any whitespace/newlines

        // Decode the Base64 string
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyPEM);

        // Create a KeyFactory and generate the PublicKey
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }


    // Generate a new AES key for encrypting patient data
    public SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(AES_KEY_SIZE);
        return keyGen.generateKey();
    }

    // Encrypt data using AES with a generated SecretKey
    public byte[] encryptData(String data, SecretKey aesKey, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, parameterSpec);
        return cipher.doFinal(data.getBytes());
    }

    // Encrypt the AES key with the RSA public key
    public byte[] encryptAESKeyWithRSA(SecretKey aesKey) throws Exception {
        PublicKey publicKey = loadRSAPublicKey();
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.WRAP_MODE, publicKey);
        return cipher.wrap(aesKey);
    }

    private PrivateKey loadRSAPrivateKey() throws Exception {
        byte[] keyBytes = Files.readAllBytes(Paths.get("src/main/resources/keys/privateKey.pem"));
        String privateKeyPEM = new String(keyBytes)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decodedKey = Base64.getDecoder().decode(privateKeyPEM);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }


    public String decryptPatientData(String encryptedData, String encryptedAESKey, String iv) throws Exception {
        // Step 1: Load the RSA private key
        PrivateKey privateKey = loadRSAPrivateKey();

        // Step 2: Decrypt the AES key using the RSA private key
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        rsaCipher.init(Cipher.UNWRAP_MODE, privateKey);
        byte[] encryptedAESKeyBytes = Base64.getDecoder().decode(encryptedAESKey);
        SecretKey aesKey = (SecretKey) rsaCipher.unwrap(encryptedAESKeyBytes, "AES", Cipher.SECRET_KEY);

        // Step 3: Decrypt the data using the AES key
        byte[] ivBytes = Base64.getDecoder().decode(iv);
        Cipher aesCipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, ivBytes);
        aesCipher.init(Cipher.DECRYPT_MODE, aesKey, gcmSpec);
        byte[] encryptedDataBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedDataBytes = aesCipher.doFinal(encryptedDataBytes);

        // Step 4: Convert decrypted data to string and return
        return new String(decryptedDataBytes);
    }

}