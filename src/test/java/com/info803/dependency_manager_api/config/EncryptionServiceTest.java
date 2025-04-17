package com.info803.dependency_manager_api.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class EncryptionServiceTest {

    @InjectMocks
    private EncryptionService encryptionService;

    private String secretKey;
    private String testString;

    @BeforeEach
    void setUp() {
        // Generate a valid base64-encoded secret key
        byte[] keyBytes = new byte[16];
        for (int i = 0; i < 16; i++) {
            keyBytes[i] = (byte) i;
        }
        secretKey = Base64.getEncoder().encodeToString(keyBytes);

        // Set the secret key using reflection
        ReflectionTestUtils.setField(encryptionService, "secretKey", secretKey);
        ReflectionTestUtils.setField(encryptionService, "aesKeySpec", new SecretKeySpec(keyBytes, 0, 16, "AES"));

        testString = "test-string";
    }

    @Test
    void encrypt_ShouldEncryptString() {
        // Act
        String result = encryptionService.encrypt(testString);

        // Assert
        assertNotNull(result);
        assertNotEquals(testString, result);
        assertDoesNotThrow(() -> Base64.getDecoder().decode(result));
    }

    @Test
    void encrypt_WhenStringIsAlreadyEncrypted_ShouldThrowException() {
        // Arrange
        String alreadyEncrypted = Base64.getEncoder().encodeToString("test".getBytes());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> encryptionService.encrypt(alreadyEncrypted));
    }

    @Test
    void decrypt_ShouldDecryptString() {
        // Arrange
        String encrypted = encryptionService.encrypt(testString);

        // Act
        String result = encryptionService.decrypt(encrypted);

        // Assert
        assertEquals(testString, result);
    }

    @Test
    void decrypt_WhenStringIsNotEncrypted_ShouldThrowException() {
        // Act & Assert
        assertThrows(RuntimeException.class, () -> encryptionService.decrypt(testString));
    }
} 