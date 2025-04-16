package com.example.itviecbackend.controller;

import com.example.itviecbackend.dtos.LoginRequest;
import com.example.itviecbackend.dtos.AuthResponse;
import com.example.itviecbackend.entities.User;
import com.example.itviecbackend.services.UserService;
import com.example.itviecbackend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            if (userService.verifyPassword(loginRequest.getUsername(), loginRequest.getPassword())) {
                return userService.findByUsername(loginRequest.getUsername())
                        .map(user -> {
                            String token = jwtUtil.generateJwtToken(loginRequest.getUsername());

                            ResponseCookie cookie = ResponseCookie.from("authToken", token)
                                .httpOnly(true)
                                .secure(true) // Set to true for production
                                .path("/")
                                .maxAge(Duration.ofDays(7))
                                .sameSite("Strict") // Use Strict instead of Lax
                                .build();

                            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
                            return ResponseEntity.ok(new AuthResponse(true, "Login successful", userService.convertToDto(user)));
                        })
                        .orElse(ResponseEntity.badRequest().body(new AuthResponse(false, "User not found")));
            }
            return ResponseEntity.badRequest().body(new AuthResponse(false, "Invalid credentials"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new AuthResponse(false, "Server error"));
        }
    }

    @GetMapping("/check-auth")
    public ResponseEntity<?> checkAuth(@CookieValue(name = "authToken", required = false) String token) {
        try {
            if (token == null || !jwtUtil.validateToken(token)) {
                return ResponseEntity.ok(new AuthResponse(false, "Not authenticated"));
            }
            
            String username = jwtUtil.getUsernameFromToken(token);
            return userService.findByUsername(username)
                    .map(user -> ResponseEntity.ok(new AuthResponse(true, "Authenticated", userService.convertToDto(user))))
                    .orElse(ResponseEntity.ok(new AuthResponse(false, "User not found")));
        } catch (Exception e) {
            return ResponseEntity.ok(new AuthResponse(false, "Not authenticated"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(name = "authToken", required = false) String token, HttpServletResponse response) {
        try {
            // Create an expired cookie with the same settings as the login cookie
            ResponseCookie cookie = ResponseCookie.from("authToken", "")
                .httpOnly(true)
                .secure(false) // Set to false for local development
                .path("/")
                .maxAge(0) // Expire immediately
                .sameSite("Lax")
                .build();

            // Add the cookie to the response
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            // If we have a token, we can optionally blacklist it or perform other cleanup
            if (token != null) {
                // Here you could add the token to a blacklist if needed
                // jwtUtil.blacklistToken(token);
            }

            return ResponseEntity.ok().body(new AuthResponse(true, "Logged out successfully"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new AuthResponse(false, "Error during logout: " + e.getMessage()));
        }
    }
} 