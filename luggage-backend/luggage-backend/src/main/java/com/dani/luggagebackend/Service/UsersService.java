package com.dani.luggagebackend.Service;

import com.dani.luggagebackend.DTO.*;
import com.dani.luggagebackend.Model.Booking;
import com.dani.luggagebackend.Model.Location;
import com.dani.luggagebackend.Model.Users;
import com.dani.luggagebackend.Repo.BookingRepo;
import com.dani.luggagebackend.Repo.LocationRepo;
import com.dani.luggagebackend.Repo.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UsersService {

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private LocationRepo locationRepo;

    /**
     * Register a new user
     *
     * @param request Registration details
     * @return LoginResponse with user info
     * @throws RuntimeException if email already exists
     */
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        // Check if email already exists
        if (usersRepo.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Create new user
        // NOTE: In production, use BCrypt to hash passwords
        Users user = Users.builder()
                .email(request.getEmail())
                .passwordHash(request.getPassword()) // In production: passwordEncoder.encode(request.getPassword())
                .fullName(request.getFullName())
                .role(request.getRole() != null ? request.getRole() : Users.Role.USER)
                .build();

        Users savedUser = usersRepo.save(user);

        return LoginResponse.builder()
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .fullName(savedUser.getFullName())
                .role(savedUser.getRole())
                .message("User registered successfully")
                .build();
    }

    /**
     * Login user
     *
     * @param request Login credentials
     * @return LoginResponse with user info
     * @throws RuntimeException if credentials are invalid
     */
    public LoginResponse login(LoginRequest request) {
        Users user = usersRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // NOTE: In production, use BCrypt to compare passwords
        // passwordEncoder.matches(request.getPassword(), user.getPasswordHash())
        if (!user.getPasswordHash().equals(request.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        return LoginResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .message("Login successful")
                .build();
    }

    /**
     * Get user by ID
     *
     * @param userId User ID
     * @return UserResponse if found
     */
    public Optional<UserResponse> getUserById(UUID userId) {
        return usersRepo.findById(userId).map(this::convertToResponse);
    }

    /**
     * Get user by email
     *
     * @param email User email
     * @return UserResponse if found
     */
    public Optional<UserResponse> getUserByEmail(String email) {
        return usersRepo.findByEmail(email).map(this::convertToResponse);
    }

    /**
     * Get all users (admin only)
     *
     * @return List of all users
     */
    public List<UserResponse> getAllUsers() {
        return usersRepo.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get users by role
     *
     * @param role User role
     * @return List of users with specified role
     */
    public List<UserResponse> getUsersByRole(Users.Role role) {
        return usersRepo.findByRole(role).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update user profile
     *
     * @param userId  User ID
     * @param request Update request
     * @return Updated UserResponse
     * @throws RuntimeException if user not found or email already exists
     */
    @Transactional
    public UserResponse updateUser(UUID userId, UpdateUserRequest request) {
        Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update email if provided and different
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (usersRepo.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(request.getEmail());
        }

        // Update full name if provided
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        Users updatedUser = usersRepo.save(user);
        return convertToResponse(updatedUser);
    }

    /**
     * Change user password
     *
     * @param userId  User ID
     * @param request Password change request
     * @throws RuntimeException if user not found or current password is wrong
     */
    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify current password
        // NOTE: In production, use BCrypt
        if (!user.getPasswordHash().equals(request.getCurrentPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Check if new password matches confirmation
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("New password and confirmation do not match");
        }

        // Update password
        // NOTE: In production, hash with BCrypt
        user.setPasswordHash(request.getNewPassword());
        usersRepo.save(user);
    }

    /**
     * Upgrade user role to HOST
     *
     * @param userId User ID
     * @return Updated UserResponse
     * @throws RuntimeException if user not found or already a HOST
     */
    @Transactional
    public UserResponse upgradeToHost(UUID userId) {
        Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == Users.Role.HOST) {
            throw new RuntimeException("User is already a host");
        }

        if (user.getRole() == Users.Role.ADMIN) {
            throw new RuntimeException("Cannot change admin role");
        }

        user.setRole(Users.Role.HOST);
        Users updatedUser = usersRepo.save(user);
        return convertToResponse(updatedUser);
    }

    /**
     * Delete user account
     *
     * @param userId User ID
     * @throws RuntimeException if user not found
     */
    @Transactional
    public void deleteUser(UUID userId) {
        Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // In production, you might want to:
        // 1. Check for active bookings
        // 2. Soft delete instead of hard delete
        // 3. Notify affected parties
        // 4. Handle cascade deletions properly

        usersRepo.delete(user);
    }

    /**
     * Search users by name or email
     *
     * @param query Search query
     * @return List of matching users
     */
    public List<UserResponse> searchUsers(String query) {
        List<Users> users = usersRepo.findByFullNameContainingIgnoreCase(query);

        // Also search by email
        usersRepo.findByEmail(query).ifPresent(user -> {
            if (!users.contains(user)) {
                users.add(user);
            }
        });

        return users.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Check if user exists by email
     *
     * @param email Email to check
     * @return true if exists
     */
    public boolean emailExists(String email) {
        return usersRepo.existsByEmail(email);
    }

    /**
     * Convert Users entity to UserResponse DTO
     */
    private UserResponse convertToResponse(Users user) {
        // Count bookings for this user
        Integer totalBookings = bookingRepo.findAll().stream()
                .filter(b -> b.getUser().getId().equals(user.getId()))
                .toList()
                .size();

        // Count locations if user is a host
        Integer totalLocations = 0;
        if (user.getRole() == Users.Role.HOST) {
            totalLocations = locationRepo.findAll().stream()
                    .filter(l -> l.getHost().getId().equals(user.getId()))
                    .toList()
                    .size();
        }

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .totalBookings(totalBookings)
                .totalLocations(totalLocations)
                .build();
    }
}