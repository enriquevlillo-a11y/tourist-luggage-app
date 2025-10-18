package com.dani.luggagebackend.Repo;

import com.dani.luggagebackend.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UsersRepo extends JpaRepository<Users, UUID> {
}
