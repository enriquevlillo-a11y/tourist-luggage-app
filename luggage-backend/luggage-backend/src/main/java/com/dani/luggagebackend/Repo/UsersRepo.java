package com.dani.luggagebackend.Repo;

import com.dani.luggagebackend.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsersRepo extends JpaRepository<Users, UUID> {

    /**
     * Find user by email (for login and duplicate checking)
     */
    Optional<Users> findByEmail(String email);

    /**
     * Check if email already exists
     */
    boolean existsByEmail(String email);

    /**
     * Find users by role
     */
    List<Users> findByRole(Users.Role role);

    /**
     * Find users by role and email containing (for admin search)
     */
    List<Users> findByRoleAndEmailContainingIgnoreCase(Users.Role role, String email);

    /**
     * Find users by full name containing (case-insensitive search)
     */
    List<Users> findByFullNameContainingIgnoreCase(String name);
}
