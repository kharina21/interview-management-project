package com.example.itviecbackend.repository;

import com.example.itviecbackend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByAuthToken(String authToken);
    Optional<User> findByEmailAndActive(String email, boolean active);
    Optional<User> findByResetPasswordToken(String resetPasswordToken);
}