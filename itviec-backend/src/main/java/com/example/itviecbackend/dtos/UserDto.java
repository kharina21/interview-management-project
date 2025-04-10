package com.example.itviecbackend.dtos;

import lombok.Data;

import java.util.Set;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private Set<String> roles; // Chỉ trả về tên role
}