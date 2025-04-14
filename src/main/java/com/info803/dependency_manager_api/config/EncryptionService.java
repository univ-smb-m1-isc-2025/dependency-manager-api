package com.info803.dependency_manager_api.config;

import jakarta.annotation.PostConstruct;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

@Service
public class EncryptionService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    private SecretKeySpec aesKeySpec;

    @PostConstruct
    private void init() {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(secretKey);
            aesKeySpec = new SecretKeySpec(keyBytes, 0, 16, "AES"); // Use first 128 bits (or 256 if supported)
        } catch (Exception e) {
            throw new RuntimeException("Error loading encryption key : " + e.getMessage());
        }
    }

    public String encrypt(String string) {
        if (isBase64(string)) {
            throw new RuntimeException("String already encrypted");
        }

        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKeySpec);
            byte[] encryptedBytes = cipher.doFinal(string.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting string : " + e.getMessage());
        }
    }

    public String decrypt(String string) {
        if (!isBase64(string)) {
            throw new RuntimeException("String not encrypted");
        }

        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKeySpec);
            byte[] decoded = Base64.getDecoder().decode(string);
            byte[] decryptedBytes = cipher.doFinal(decoded);
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting string : " + e.getMessage());
        }
    }

    private boolean isBase64(String token) {
        try {
            Base64.getDecoder().decode(token);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
