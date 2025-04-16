package com.example.itviecbackend.services;

import com.example.itviecbackend.dtos.UserDto;
import com.example.itviecbackend.entities.Role;
import com.example.itviecbackend.entities.User;
import com.example.itviecbackend.repository.UserRepository;
import com.example.itviecbackend.utils.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        // Hash the password before saving
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
            
            // Only update password if it's provided
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
}