package com.dani.luggagebackend.DTO;

import com.dani.luggagebackend.Model.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {
    private UUID userId;
    private String email;
    private String fullName;
    private Users.Role role;
    private String message;
    private String token; // JWT token for authentication
}