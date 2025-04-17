package com.example.itviecbackend.services;

import com.example.itviecbackend.dtos.UserDto;
import com.example.itviecbackend.entities.Role;
import com.example.itviecbackend.entities.User;
import com.example.itviecbackend.repository.UserRepository;
import com.example.itviecbackend.utils.PasswordUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class  UserService {
    @Value("${app.reset-password-link}")
    private String resetPasswordLinkBaseUrl;

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    @Autowired
    public UserService(UserRepository userRepository, JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<UserDto> getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToDto);
    }

    public UserDto createUser(User user) {
        user.setPassword(PasswordUtils.hashPassword(user.getPassword()));
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    public UserDto updateUser(Long id, User userDetails) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            User existingUser = user.get();
            existingUser.setUsername(userDetails.getUsername());
            existingUser.setFirstname(userDetails.getFirstname());
            existingUser.setLastname(userDetails.getLastname());
            existingUser.setEmail(userDetails.getEmail());

            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                existingUser.setPassword(PasswordUtils.hashPassword(userDetails.getPassword()));
            }

            User updatedUser = userRepository.save(existingUser);
            return convertToDto(updatedUser);
        }
        return null;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean verifyPassword(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.isPresent() && PasswordUtils.verifyPassword(password, user.get().getPassword());
    }

    public void storeAuthToken(Long userId, String token) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setAuthToken(token);
            userRepository.save(user);
        });
    }

    public Optional<User> findByAuthToken(String token) {
        return userRepository.findByAuthToken(token);
    }

    public void clearAuthToken(String token) {
        userRepository.findByAuthToken(token)
                .ifPresent(user -> {
                    user.setAuthToken(null);
                    userRepository.save(user);
                });
    }

    public UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFirstname(user.getFirstname());
        dto.setLastname(user.getLastname());
        dto.setEmail(user.getEmail());
        dto.setRoles(user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet()));
        return dto;
    }

    public boolean checkIfEmailExists(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!Pattern.matches(emailRegex, email)) {
            return false;
        }
        return userRepository.findByEmailAndActive(email, true).isPresent();
    }

    public void sendResetPasswordLink(String email) {
        Optional<User> userOptional = userRepository.findByEmailAndActive(email, true);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String token = UUID.randomUUID().toString();
            user.setResetPasswordToken(token);
            user.setResetPasswordTokenExpiry(LocalDateTime.now().plusHours(24));
            userRepository.save(user);

            String resetLink = resetPasswordLinkBaseUrl + "?token=" + token;
            sendEmail(email, "Reset Your Password", "Click the link to reset your password: " + resetLink);
        }
    }

    public boolean validateResetPasswordLink(String token) {
        return userRepository.findByResetPasswordToken(token)
                .map(user -> user.getResetPasswordTokenExpiry().isAfter(LocalDateTime.now()))
                .orElse(false);
    }

    public void resetPassword(String token, String newPassword) {
        Optional<User> userOptional = userRepository.findByResetPasswordToken(token);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getResetPasswordTokenExpiry().isAfter(LocalDateTime.now())) {
                user.setPassword(PasswordUtils.hashPassword(newPassword));
                user.setResetPasswordToken(null);
                user.setResetPasswordTokenExpiry(null);
                user.setAuthToken(null); // Clear auth token if needed
                userRepository.save(user);
            } else {
                throw new IllegalArgumentException("Reset password token has expired.");
            }
        } else {
            throw new IllegalArgumentException("Invalid reset password token.");
        }
    }

    private void sendEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
