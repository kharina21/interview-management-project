package com.example.itviecbackend.dtos;

import lombok.Data;

@Data
public class AuthResponse {
    private boolean authenticated;
    private String message;
    private UserDto user;

    public AuthResponse(boolean authenticated, String message) {
        this.authenticated = authenticated;
        this.message = message;
    }

    public AuthResponse(boolean authenticated, String message, UserDto user) {
        this.authenticated = authenticated;
        this.message = message;
        this.user = user;
    }
} 