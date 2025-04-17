package com.example.itviecbackend.controller;

import com.example.itviecbackend.dtos.AuthResponse;
import com.example.itviecbackend.dtos.LoginRequest;
import com.example.itviecbackend.dtos.UserDto;
import com.example.itviecbackend.services.UserService;
import com.example.itviecbackend.utils.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.time.Duration;

@RestController
@RequestMapping("/api")
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
                            try {
                                // Debug user state
                                System.out.println("User ID: " + user.getId());
                                System.out.println("Username: " + user.getUsername());
                                System.out.println("Email: " + user.getEmail());
                                System.out.println("Roles count: " + (user.getRoles() != null ? user.getRoles().size() : "null"));

                                String token = jwtUtil.generateJwtToken(loginRequest.getUsername());
                                System.out.println("Token generated successfully");

                                ResponseCookie cookie = ResponseCookie.from("authToken", token)
                                    .httpOnly(true)
                                    .secure(false) // Set to false for local development
                                    .path("/")
                                    .maxAge(Duration.ofDays(7))
                                    .sameSite("Strict")
                                    .build();
                                response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
                                System.out.println("Cookie set successfully");

                                UserDto dto = userService.convertToDto(user);
                                System.out.println("DTO created successfully: " + dto);

                                AuthResponse authResponse = new AuthResponse(true, "Login successful", dto);
                                System.out.println("AuthResponse created: " + authResponse);

                                return ResponseEntity.ok(authResponse);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                System.out.println("Stack trace: " + ex.getClass().getName() + ": " + ex.getMessage());
                                return ResponseEntity.internalServerError()
                                    .body(new AuthResponse(false, "Error during login: " + ex.getMessage()));
                            }
                        })
                        .orElse(ResponseEntity.badRequest().body(new AuthResponse(false, "User not found")));
            }
            return ResponseEntity.badRequest().body(new AuthResponse(false, "Invalid credentials"));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Outer exception: " + e.getClass().getName() + ": " + e.getMessage());
            return ResponseEntity.internalServerError().body(new AuthResponse(false, "Server error: " + e.getMessage()));
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

            return ResponseEntity.ok().body(new AuthResponse(true, "Logged out successfully"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new AuthResponse(false, "Error during logout: " + e.getMessage()));
        }
    }

    // Forgot Password API
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        boolean emailExists = userService.checkIfEmailExists(request.getEmail());
        if (!emailExists) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("The email address doesn't exist. Please try again.");
        }

        userService.sendResetPasswordLink(request.getEmail());
        return ResponseEntity.ok("A reset password link has been sent to your email.");
    }

    @GetMapping("/reset-password")
    public RedirectView redirectToFrontend(@RequestParam("token") String token) {
        return new RedirectView("http://localhost:5173/reset-password?token=" + token);
    }

    // Reset Password API
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        boolean isValidLink = userService.validateResetPasswordLink(request.getToken());
        if (!isValidLink) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("The reset password link is invalid or has expired.");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Passwords do not match.");
        }

        userService.resetPassword(request.getToken(), request.getPassword());
        return ResponseEntity.ok("Your password has been reset successfully.");
    }

    @Data
    // DTO for Forgot Password Request
    public static class ForgotPasswordRequest {
        @NotBlank(message = "Email is required.")
        @Email(message = "Invalid email format.")
        private String email;
    }

    @Data
    // DTO for Reset Password Request
    public static class ResetPasswordRequest {
        @NotBlank(message = "Password is required.")
        private String password;

        @NotBlank(message = "Confirm password is required.")
        private String confirmPassword;

        @NotBlank(message = "Token is required.")
        private String token;
    }
}