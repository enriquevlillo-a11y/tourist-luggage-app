package com.dani.luggagebackend.Repo;

import com.dani.luggagebackend.Model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BookingRepo extends JpaRepository<Booking, UUID> {
}
