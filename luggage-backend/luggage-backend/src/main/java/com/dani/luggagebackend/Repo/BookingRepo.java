package com.dani.luggagebackend.Repo;

import com.dani.luggagebackend.Model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookingRepo extends JpaRepository<Booking, UUID> {
}
