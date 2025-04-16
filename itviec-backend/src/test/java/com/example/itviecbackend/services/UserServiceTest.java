package com.example.itviecbackend.services;

import com.example.itviecbackend.entities.Role;
import com.example.itviecbackend.entities.User;
import com.example.itviecbackend.repository.RoleRepository;
import com.example.itviecbackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User testUser;
    private Role adminRole;

    @BeforeEach
    void setUp() {
        // Create and save test role
        adminRole = new Role();
        adminRole.setName("ADMIN");
        adminRole = roleRepository.save(adminRole);

        // Create test user with saved role
        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setFirstname("Test");
        testUser.setLastname("User");
        testUser.setEmail("test@example.com");
        testUser.setRoles(roles);
    }

    @Test
    void testCreateUser() {
        // Create user
        userService.createUser(testUser);

        // Verify user was created
        Optional<User> foundUser = userRepository.findByUsername("testuser");
        assertTrue(foundUser.isPresent());
        assertEquals("test@example.com", foundUser.get().getEmail());
        assertTrue(foundUser.get().getPassword().startsWith("$2a$")); // Verify password was hashed
    }

    @Test
    void testVerifyPassword() {
        // Create user first
        userService.createUser(testUser);

        // Test correct password
        assertTrue(userService.verifyPassword("testuser", "password123"));

        // Test incorrect password
        assertFalse(userService.verifyPassword("testuser", "wrongpassword"));
    }

    @Test
    void testFindByUsername() {
        // Create user first
        userService.createUser(testUser);

        // Test finding existing user
        Optional<User> foundUser = userService.findByUsername("testuser");
        assertTrue(foundUser.isPresent());
        assertEquals("test@example.com", foundUser.get().getEmail());

        // Test finding non-existent user
        Optional<User> notFoundUser = userService.findByUsername("nonexistent");
        assertFalse(notFoundUser.isPresent());
    }
} 