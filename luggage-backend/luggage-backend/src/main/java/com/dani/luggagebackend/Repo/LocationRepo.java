package com.dani.luggagebackend.Repo;

import com.dani.luggagebackend.Model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LocationRepo extends JpaRepository<Location, UUID> {

    /**
     * Finds all locations within a given radius using the Haversine formula.
     * The formula calculates the great-circle distance between two points on a sphere.
     *
     * @param latitude User's current latitude
     * @param longitude User's current longitude
     * @param radiusKm Search radius in kilometers
     * @return List of locations within the specified radius
     */
    @Query(value = """
        SELECT * FROM locations l
        WHERE (6371 * acos(
            cos(radians(:latitude)) * cos(radians(l.lat)) *
            cos(radians(l.lng) - radians(:longitude)) +
            sin(radians(:latitude)) * sin(radians(l.lat))
        )) <= :radiusKm
        ORDER BY (6371 * acos(
            cos(radians(:latitude)) * cos(radians(l.lat)) *
            cos(radians(l.lng) - radians(:longitude)) +
            sin(radians(:latitude)) * sin(radians(l.lat))
        ))
        """, nativeQuery = true)
    List<Location> findLocationsWithinRadius(
        @Param("latitude") Double latitude,
        @Param("longitude") Double longitude,
        @Param("radiusKm") Double radiusKm
    );

    /**
     * Finds all locations owned by a specific host.
     *
     * @param hostId The host's user ID
     * @return List of locations owned by the host
     */
    List<Location> findByHostId(UUID hostId);
}
