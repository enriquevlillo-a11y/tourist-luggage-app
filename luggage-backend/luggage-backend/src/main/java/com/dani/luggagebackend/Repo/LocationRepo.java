package com.dani.luggagebackend.Repo;

import com.dani.luggagebackend.Model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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

    /**
     * Finds all active locations.
     *
     * @return List of active locations
     */
    List<Location> findByIsActiveTrue();

    /**
     * Search locations by name or address (case-insensitive).
     *
     * @param name Keyword to search in name
     * @param address Keyword to search in address
     * @return List of locations matching the search
     */
    List<Location> findByNameContainingIgnoreCaseOrAddressContainingIgnoreCase(String name, String address);

    /**
     * Search active locations by name or address (case-insensitive).
     *
     * @param name Keyword to search in name
     * @param address Keyword to search in address
     * @return List of active locations matching the search
     */
    List<Location> findByIsActiveTrueAndNameContainingIgnoreCaseOrAddressContainingIgnoreCase(String name, String address);

    /**
     * Filter locations by price range.
     *
     * @param minPrice Minimum price per hour
     * @param maxPrice Maximum price per hour
     * @return List of locations within price range
     */
    List<Location> findByPricePerHourBetween(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * Filter locations by minimum capacity.
     *
     * @param minCapacity Minimum capacity required
     * @return List of locations with at least the specified capacity
     */
    List<Location> findByCapacityGreaterThanEqual(Integer minCapacity);

    /**
     * Find all locations in a specific city.
     *
     * @param city City name
     * @return List of locations in the city
     */
    List<Location> findByCityIgnoreCase(String city);

    /**
     * Find all active locations in a specific city.
     *
     * @param city City name
     * @return List of active locations in the city
     */
    List<Location> findByIsActiveTrueAndCityIgnoreCase(String city);

    /**
     * Get all unique cities.
     *
     * @return List of distinct city names
     */
    @Query("SELECT DISTINCT l.city FROM Location l WHERE l.city IS NOT NULL ORDER BY l.city")
    List<String> findAllCities();

    /**
     * Find most popular locations based on booking count.
     *
     * @return List of locations ordered by popularity
     */
    @Query("""
        SELECT l FROM Location l
        LEFT JOIN Booking b ON b.location.id = l.id
        WHERE l.isActive = true
        GROUP BY l.id
        ORDER BY COUNT(b.id) DESC
        """)
    List<Location> findMostPopularLocations();

    /**
     * Find active locations within radius with filters.
     *
     * @param latitude User's current latitude
     * @param longitude User's current longitude
     * @param radiusKm Search radius in kilometers
     * @param minPrice Minimum price filter (nullable)
     * @param maxPrice Maximum price filter (nullable)
     * @param minCapacity Minimum capacity filter (nullable)
     * @return List of filtered locations within radius
     */
    @Query(value = """
        SELECT * FROM locations l
        WHERE l.is_active = true
        AND (6371 * acos(
            cos(radians(:latitude)) * cos(radians(l.lat)) *
            cos(radians(l.lng) - radians(:longitude)) +
            sin(radians(:latitude)) * sin(radians(l.lat))
        )) <= :radiusKm
        AND (:minPrice IS NULL OR l.price_per_hour >= :minPrice)
        AND (:maxPrice IS NULL OR l.price_per_hour <= :maxPrice)
        AND (:minCapacity IS NULL OR l.capacity >= :minCapacity)
        ORDER BY (6371 * acos(
            cos(radians(:latitude)) * cos(radians(l.lat)) *
            cos(radians(l.lng) - radians(:longitude)) +
            sin(radians(:latitude)) * sin(radians(l.lat))
        ))
        """, nativeQuery = true)
    List<Location> findActiveLocationsWithinRadiusAndFilters(
        @Param("latitude") Double latitude,
        @Param("longitude") Double longitude,
        @Param("radiusKm") Double radiusKm,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        @Param("minCapacity") Integer minCapacity
    );
}
