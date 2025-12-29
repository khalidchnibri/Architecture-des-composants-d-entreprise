package com.example.eventplatform.user;

import com.example.eventplatform.security.CustomUserDetailsService;
import com.example.eventplatform.security.JwtUtil;
import com.example.eventplatform.user.dto.AuthRequest;
import com.example.eventplatform.user.dto.AuthResponse;
import com.example.eventplatform.user.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // Déterminer le rôle : ORGANIZER ou USER (par défaut)
        Role userRole = Role.ROLE_USER;
        if (request.getRole() != null && request.getRole().equalsIgnoreCase("ORGANIZER")) {
            userRole = Role.ROLE_ORGANIZER;
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(userRole))
                .build();

        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtUtil.generateToken(userDetails);

        return new AuthResponse(
                token,
                user.getUsername(),
                user.getEmail(),
                user.getRoles().stream().map(Enum::name).collect(Collectors.toSet())
        );
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtUtil.generateToken(userDetails);
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new AuthResponse(
                token,
                user.getUsername(),
                user.getEmail(),
                user.getRoles().stream().map(Enum::name).collect(Collectors.toSet())
        );
    }
}


