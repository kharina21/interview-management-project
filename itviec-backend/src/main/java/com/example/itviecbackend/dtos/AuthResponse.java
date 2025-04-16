package com.example.itviecbackend.dtos;

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

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }
} 