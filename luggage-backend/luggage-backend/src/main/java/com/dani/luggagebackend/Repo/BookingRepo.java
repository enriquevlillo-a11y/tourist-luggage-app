package com.dani.luggagebackend.Repo;

import com.dani.luggagebackend.Model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepo extends JpaRepository<Booking, UUID> {

    /**
     * Finds all bookings for a specific location.
     *
     * @param locationId The location ID
     * @return List of bookings for the location
     */
    List<Booking> findByLocationId(UUID locationId);

    /**
     * Finds all bookings for locations owned by a specific host.
     * This allows hosts to see all bookings across all their locations.
     *
     * @param hostId The host's user ID
     * @return List of bookings for the host's locations
     */
    @Query("SELECT b FROM Booking b WHERE b.location.host.id = :hostId ORDER BY b.startTime DESC")
    List<Booking> findByLocationHostId(@Param("hostId") UUID hostId);
}
