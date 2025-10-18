package com.dani.luggagebackend.Repo;

import com.dani.luggagebackend.Model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LocationRepo extends JpaRepository<Location, UUID> {
}
