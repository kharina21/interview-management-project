package com.example.itviecbackend.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtils {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static String hashPassword(String password) {
        return encoder.encode(password);
    }

    public static boolean verifyPassword(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
} 