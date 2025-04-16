package com.example.itviecbackend.dtos;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
} 