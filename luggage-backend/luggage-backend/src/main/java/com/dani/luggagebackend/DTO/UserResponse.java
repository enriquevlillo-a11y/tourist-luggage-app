package com.dani.luggagebackend.DTO;

import com.dani.luggagebackend.Model.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private UUID id;
    private String email;
    private String fullName;
    private Users.Role role;
    private Instant createdAt;
    private Instant updatedAt;

    // Statistics for users (optional)
    private Integer totalBookings;
    private Integer totalLocations; // For hosts
}