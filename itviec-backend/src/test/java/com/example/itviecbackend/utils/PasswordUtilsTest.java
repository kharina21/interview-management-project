package com.example.itviecbackend.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordUtilsTest {

    @Test
    void testSamePasswordDifferentHashes() {
        String password = "test123";
        
        // Generate two hashes for the same password
        String hash1 = PasswordUtils.hashPassword(password);
        String hash2 = PasswordUtils.hashPassword(password);
        
        // The hashes should be different
        assertNotEquals(hash1, hash2);
        
        // But both should verify correctly
        assertTrue(PasswordUtils.verifyPassword(password, hash1));
        assertTrue(PasswordUtils.verifyPassword(password, hash2));
    }

    @Test
    void testPasswordVerification() {
        String password = "mySecretPassword";
        String wrongPassword = "wrongPassword";
        
        // Hash the password
        String hashedPassword = PasswordUtils.hashPassword(password);
        
        // Verify correct password
        assertTrue(PasswordUtils.verifyPassword(password, hashedPassword));
        
        // Verify wrong password
        assertFalse(PasswordUtils.verifyPassword(wrongPassword, hashedPassword));
    }

    @Test
    void testSaltDependency() {
        String password = "testPassword";
        
        // Create two different hashes for the same password
        String hash1 = PasswordUtils.hashPassword(password);
        String hash2 = PasswordUtils.hashPassword(password);
        
        // Extract the salt parts (first 29 characters after $2a$10$)
        String salt1 = hash1.substring(7, 29);
        String salt2 = hash2.substring(7, 29);
        
        // The salts should be different
        assertNotEquals(salt1, salt2);
        
        // Each hash only works with its own salt
        assertTrue(PasswordUtils.verifyPassword(password, hash1));
        assertTrue(PasswordUtils.verifyPassword(password, hash2));
        
        // If we try to use hash1's salt with hash2's hash, it won't work
        // (This is why we can't verify without the correct salt)
        assertFalse(PasswordUtils.verifyPassword(password, 
            "$2a$10$" + salt1 + hash2.substring(29)));
    }

    @Test
    void testVerifyPasswordWithHash() {
        // Example BCrypt hash (this is a hash of "password123")
        String storedHash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        
        // Test with correct password
        assertTrue(PasswordUtils.verifyPassword("password123", storedHash));
        
        // Test with incorrect password
        assertFalse(PasswordUtils.verifyPassword("wrongpassword", storedHash));
    }
} 