package com.example.eventplatform.config;

import com.example.eventplatform.user.Role;
import com.example.eventplatform.user.User;
import com.example.eventplatform.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = User.builder()
                        .username("admin")
                        .email("admin@event.com")
                        .password(passwordEncoder.encode("admin123"))
                        .roles(Set.of(Role.ROLE_ADMIN, Role.ROLE_ORGANIZER))
                        .build();
                userRepository.save(admin);
            }
        };
    }
}


