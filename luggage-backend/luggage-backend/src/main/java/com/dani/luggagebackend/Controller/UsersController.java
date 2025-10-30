package com.dani.luggagebackend.Controller;

import com.dani.luggagebackend.DTO.*;
import com.dani.luggagebackend.Model.Users;
import com.dani.luggagebackend.Service.UsersService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/api/users")
public class
UsersController {

    @Autowired
    private UsersService usersService;

    /**
     * Register a new user account
     *
     * Example request body:
     * {
     *   "email": "user@example.com",
     *   "password": "password123",
     *   "fullName": "John Doe",
     *   "role": "USER"
     * }
     *
     * @param request Registration details
     * @return LoginResponse with user info and HTTP 201
     */
    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            LoginResponse response = usersService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            // In production, use proper error handling
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Login user
     *
     * Example request body:
     * {
     *   "email": "user@example.com",
     *   "password": "password123"
     * }
     *
     * NOTE: In production, this would return a JWT token instead of user details
     *
     * @param request Login credentials
     * @return LoginResponse with user info
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = usersService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Get current user profile
     * Uses the X-User-Id header to identify the user
     *
     * NOTE: In production, this would use JWT token authentication
     *
     * @param userId User ID from header
     * @return UserResponse with profile info
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@RequestHeader("X-User-Id") UUID userId) {
        return usersService.getUserById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get user by ID
     *
     * Example: GET /api/users/{userId}
     *
     * @param userId User ID
     * @return UserResponse if found
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID userId) {
        return usersService.getUserById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all users (admin endpoint)
     *
     * Example: GET /api/users
     *
     * NOTE: In production, this should be restricted to admins only
     *
     * @return List of all users
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = usersService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Get users by role
     *
     * Example: GET /api/users/role/HOST
     *
     * @param role User role (USER, HOST, or ADMIN)
     * @return List of users with specified role
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable Users.Role role) {
        List<UserResponse> users = usersService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    /**
     * Update user profile
     *
     * Example request body:
     * {
     *   "email": "newemail@example.com",
     *   "fullName": "John Updated Doe"
     * }
     *
     * NOTE: User can only update their own profile
     * In production, verify that the authenticated user matches the userId
     *
     * @param userId  User ID from header
     * @param request Update request
     * @return Updated UserResponse
     */
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID userId,
            @RequestHeader("X-User-Id") UUID authenticatedUserId,
            @Valid @RequestBody UpdateUserRequest request) {

        // Verify user is updating their own profile
        if (!userId.equals(authenticatedUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            UserResponse user = usersService.updateUser(userId, request);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Change user password
     *
     * Example request body:
     * {
     *   "currentPassword": "oldpassword",
     *   "newPassword": "newpassword123",
     *   "confirmPassword": "newpassword123"
     * }
     *
     * @param userId  User ID from header
     * @param request Password change request
     * @return Success message
     */
    @PutMapping("/{userId}/password")
    public ResponseEntity<Map<String, String>> changePassword(
            @PathVariable UUID userId,
            @RequestHeader("X-User-Id") UUID authenticatedUserId,
            @Valid @RequestBody ChangePasswordRequest request) {

        // Verify user is changing their own password
        if (!userId.equals(authenticatedUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            usersService.changePassword(userId, request);
            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Upgrade user to HOST role
     *
     * Example: PATCH /api/users/{userId}/upgrade-to-host
     *
     * @param userId User ID to upgrade
     * @return Updated UserResponse
     */
    @PatchMapping("/{userId}/upgrade-to-host")
    public ResponseEntity<UserResponse> upgradeToHost(
            @PathVariable UUID userId,
            @RequestHeader("X-User-Id") UUID authenticatedUserId) {

        // Verify user is upgrading their own account
        if (!userId.equals(authenticatedUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            UserResponse user = usersService.upgradeToHost(userId);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete user account
     *
     * Example: DELETE /api/users/{userId}
     * Header: X-User-Id: {userId}
     *
     * NOTE: In production, implement soft delete and proper cleanup
     *
     * @param userId User ID to delete
     * @return HTTP 204 on success
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable UUID userId,
            @RequestHeader("X-User-Id") UUID authenticatedUserId) {

        // Verify user is deleting their own account
        // Admins could be allowed to delete any account
        if (!userId.equals(authenticatedUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            usersService.deleteUser(userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Search users by name or email
     *
     * Example: GET /api/users/search?q=john
     *
     * NOTE: In production, restrict this to admins
     *
     * @param query Search query
     * @return List of matching users
     */
    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> searchUsers(@RequestParam String q) {
        List<UserResponse> users = usersService.searchUsers(q);
        return ResponseEntity.ok(users);
    }

    /**
     * Check if email exists
     *
     * Example: GET /api/users/check-email?email=user@example.com
     *
     * Useful for frontend validation during registration
     *
     * @param email Email to check
     * @return JSON object with "exists" boolean
     */
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        boolean exists = usersService.emailExists(email);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    /**
     * Get user by email
     *
     * Example: GET /api/users/by-email?email=user@example.com
     *
     * NOTE: In production, restrict this to admins
     *
     * @param email User email
     * @return UserResponse if found
     */
    @GetMapping("/by-email")
    public ResponseEntity<UserResponse> getUserByEmail(@RequestParam String email) {
        return usersService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}