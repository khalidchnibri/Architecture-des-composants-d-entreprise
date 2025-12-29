package com.example.eventplatform.user.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String role; // "USER" or "ORGANIZER"
}

