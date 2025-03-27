package com.info803.dependency_manager_api.infrastructure.utils;

import org.mindrot.jbcrypt.BCrypt;

public class EncryptionService {

    // Attributes

    // Constructors
    private EncryptionService() {}

    // Getters

    // Setters

    // Methods
    public static String encrypt(String string) {
        return string;
    }

    public static String decrypt(String string) {
        return string;
    }

    /**
     * Hashes the given string using the BCrypt algorithm with a specified number of salt rounds.
     *
     * @param string the string to hash
     * @return the hashed string
     */
    public static String hash(String string) {
        return BCrypt.hashpw(string, BCrypt.gensalt());
    }

    /**
     * Verifies if the given string matches the specified hash.
     *
     * @param string the plain text string to verify
     * @param hash the hashed string to compare against
     * @return true if the string matches the hash, false otherwise
     */
    public static boolean verifyHash(String string, String hash) {
        return BCrypt.checkpw(string, hash);
    }
} 