package com.example.itviecbackend.config;

import com.example.itviecbackend.entities.Role;
import com.example.itviecbackend.entities.User;
import com.example.itviecbackend.repository.RoleRepository;
import com.example.itviecbackend.repository.UserRepository;
import com.example.itviecbackend.utils.PasswordUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.HashSet;
import java.util.Set;

@Configuration
@Profile("!test") // Don't run this in test environment
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, RoleRepository roleRepository) {
        return args -> {
            // Create and save roles
            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            adminRole = roleRepository.save(adminRole);

            Role userRole = new Role();
            userRole.setName("USER");
            userRole = roleRepository.save(userRole);

            // Create test users
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);

            Set<Role> userRoles = new HashSet<>();
            userRoles.add(userRole);

            // Create admin user
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(PasswordUtils.hashPassword("admin123"));
            admin.setFirstname("Admin");
            admin.setLastname("User");
            admin.setEmail("khanh0vn1234@gmail.com");
            admin.setRoles(adminRoles);
            userRepository.save(admin);

            // Create regular user
            User testUser = new User();
            testUser.setUsername("testuser");
            testUser.setPassword(PasswordUtils.hashPassword("password123"));
            testUser.setFirstname("Test");
            testUser.setLastname("User");
            testUser.setEmail("test@example.com");
            testUser.setRoles(userRoles);
            userRepository.save(testUser);
        };
    }
} 