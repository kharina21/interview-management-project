package com.example.itviecbackend.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserPostDto {
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private String gender;
    private String address;
    private boolean active;
    private String password;
    private Set<String> roles;
}
