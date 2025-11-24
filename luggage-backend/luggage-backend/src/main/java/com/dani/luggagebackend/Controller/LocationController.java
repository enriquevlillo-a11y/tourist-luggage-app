package com.dani.luggagebackend.Controller;

import com.dani.luggagebackend.DTO.CreateLocationRequest;
import com.dani.luggagebackend.DTO.LocationResponse;
import com.dani.luggagebackend.DTO.NearbyLocationRequest;
import com.dani.luggagebackend.Service.LocationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/api/locations")
public class LocationController {

    @Autowired
    private LocationService locationService;

    /**
     * Finds storage locations near the user's current position.
     * User can specify latitude, longitude, and search radius.
     *
     * Example request body:
     * {
     * "latitude": 40.7128,
     * "longitude": -74.0060,
     * "radiusKm": 10.0
     * }
     *
     * @param request Contains user's location and desired search radius
     * @return List of nearby locations ordered by distance (closest first)
     */
    @PostMapping("/nearby")
    public ResponseEntity<List<LocationResponse>> findNearbyLocations(@RequestBody NearbyLocationRequest request) {
        // Validate input
        if (request.getLatitude() == null || request.getLongitude() == null) {
            return ResponseEntity.badRequest().build();
        }

        // Use default radius if not provided
        Double radius = request.getRadiusKm() != null ? request.getRadiusKm() : 5.0;

        List<LocationResponse> locations = locationService.findNearbyLocations(
                request.getLatitude(),
                request.getLongitude(),
                radius);

        return ResponseEntity.ok(locations);
    }

    /**
     * Gets a specific location by its ID.
     *
     * @param id Location UUID
     * @return LocationResponse if found, 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<LocationResponse> getLocationById(@PathVariable UUID id) {
        return locationService.getLocationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Gets all available storage locations with pagination.
     * Useful for admins or displaying all locations on a map.
     *
     * Example: GET /api/locations?page=0&size=20
     *
     * @param page Page number (0-indexed, default: 0)
     * @param size Page size (default: 20)
     * @return Page of locations with metadata
     */
    @GetMapping
    public ResponseEntity<Page<LocationResponse>> getAllLocations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<LocationResponse> locations = locationService.getAllLocations(pageable);
        return ResponseEntity.ok(locations);
    }

    /**
     * Creates a new location. Only hosts can create locations.
     * Uses JWT authentication to identify the host.
     *
     * Example request:
     * Header: Authorization: Bearer <jwt-token>
     * Body:
     * {
     * "name": "Downtown Luggage Storage",
     * "address": "123 Main St, New York, NY",
     * "latitude": 40.7128,
     * "longitude": -74.0060,
     * "pricePerHour": 5.00,
     * "capacity": 50,
     * "hours": "Mon-Fri: 9AM-6PM"
     * }
     *
     * @param request Location creation request
     * @return Created location with HTTP 201
     */
    @PostMapping
    public ResponseEntity<LocationResponse> createLocation(
            @Valid @RequestBody CreateLocationRequest request) {
        try {
            UUID hostId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            LocationResponse location = locationService.createLocation(hostId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(location);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Updates an existing location. Only the host who owns it can update.
     * Uses JWT authentication to identify the host.
     *
     * Example request:
     * Header: Authorization: Bearer <jwt-token>
     * Body: { ... location details ... }
     *
     * @param id      Location ID to update
     * @param request Update request
     * @return Updated location
     */
    @PutMapping("/{id}")
    public ResponseEntity<LocationResponse> updateLocation(
            @PathVariable UUID id,
            @Valid @RequestBody CreateLocationRequest request) {
        try {
            UUID hostId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            LocationResponse location = locationService.updateLocation(id, hostId, request);
            return ResponseEntity.ok(location);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Deletes a location. Only the host who owns it can delete.
     * Uses JWT authentication to identify the host.
     *
     * Example request:
     * Header: Authorization: Bearer <jwt-token>
     *
     * @param id Location ID to delete
     * @return HTTP 204 on success
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(
            @PathVariable UUID id) {
        try {
            UUID hostId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            locationService.deleteLocation(id, hostId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Gets all locations owned by a specific host.
     *
     * NOTE: In production, the hostId should come from the authenticated user's
     * session/token.
     *
     * @param hostId The host's user ID
     * @return List of locations owned by the host
     */
    @GetMapping("/host/{hostId}")
    public ResponseEntity<List<LocationResponse>> getLocationsByHost(@PathVariable UUID hostId) {
        List<LocationResponse> locations = locationService.getLocationsByHost(hostId);
        return ResponseEntity.ok(locations);
    }

    /**
     * Search locations by keyword.
     * Searches in name and address fields (case-insensitive).
     *
     * Example: GET /api/locations/search?q=downtown
     *
     * @param keyword Search term
     * @return List of matching active locations
     */
    @GetMapping("/search")
    public ResponseEntity<List<LocationResponse>> searchLocations(@RequestParam String q) {
        List<LocationResponse> locations = locationService.searchLocations(q);
        return ResponseEntity.ok(locations);
    }

    /**
     * Filter locations with multiple criteria.
     *
     * Example: GET /api/locations/filter?minPrice=5&maxPrice=15&minCapacity=20
     *
     * @param minPrice    Minimum price per hour (optional)
     * @param maxPrice    Maximum price per hour (optional)
     * @param minCapacity Minimum capacity (optional)
     * @param city        City filter (optional)
     * @return List of filtered active locations
     */
    @GetMapping("/filter")
    public ResponseEntity<List<LocationResponse>> filterLocations(
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) String city) {

        List<LocationResponse> locations;

        // Apply filters based on provided parameters
        if (city != null && !city.isEmpty()) {
            locations = locationService.getLocationsByCity(city);
        } else if (minPrice != null && maxPrice != null) {
            locations = locationService.filterByPriceRange(minPrice, maxPrice);
        } else if (minCapacity != null) {
            locations = locationService.filterByCapacity(minCapacity);
        } else {
            locations = locationService.getAllLocations(PageRequest.of(0, Integer.MAX_VALUE)).getContent();
        }

        return ResponseEntity.ok(locations);
    }

    /**
     * Find nearby locations with optional filters.
     *
     * Example: POST /api/locations/nearby/filtered
     * Body: {
     * "latitude": 40.7128,
     * "longitude": -74.0060,
     * "radiusKm": 10.0,
     * "minPrice": 5.0,
     * "maxPrice": 15.0,
     * "minCapacity": 20
     * }
     *
     * @param params Request parameters including location and filters
     * @return List of filtered nearby locations
     */
    @PostMapping("/nearby/filtered")
    public ResponseEntity<List<LocationResponse>> findNearbyWithFilters(@RequestBody Map<String, Object> params) {
        if (!params.containsKey("latitude") || !params.containsKey("longitude")) {
            return ResponseEntity.badRequest().build();
        }

        Double latitude = ((Number) params.get("latitude")).doubleValue();
        Double longitude = ((Number) params.get("longitude")).doubleValue();
        Double radiusKm = params.containsKey("radiusKm") ? ((Number) params.get("radiusKm")).doubleValue() : 5.0;

        BigDecimal minPrice = params.containsKey("minPrice") ? new BigDecimal(params.get("minPrice").toString()) : null;
        BigDecimal maxPrice = params.containsKey("maxPrice") ? new BigDecimal(params.get("maxPrice").toString()) : null;
        Integer minCapacity = params.containsKey("minCapacity") ? ((Number) params.get("minCapacity")).intValue()
                : null;

        List<LocationResponse> locations = locationService.findNearbyWithFilters(
                latitude, longitude, radiusKm, minPrice, maxPrice, minCapacity);

        return ResponseEntity.ok(locations);
    }

    /**
     * Get all unique cities with locations.
     *
     * Example: GET /api/locations/cities
     *
     * @return List of city names
     */
    @GetMapping("/cities")
    public ResponseEntity<List<String>> getAllCities() {
        List<String> cities = locationService.getAllCities();
        return ResponseEntity.ok(cities);
    }

    /**
     * Get locations by city.
     *
     * Example: GET /api/locations/city/new-york
     *
     * @param city City name
     * @return List of active locations in the city
     */
    @GetMapping("/city/{city}")
    public ResponseEntity<List<LocationResponse>> getLocationsByCity(@PathVariable String city) {
        List<LocationResponse> locations = locationService.getLocationsByCity(city);
        return ResponseEntity.ok(locations);
    }

    /**
     * Get popular locations based on booking count.
     *
     * Example: GET /api/locations/popular?limit=10
     *
     * @param limit Maximum number of locations to return (default: 10)
     * @return List of popular locations
     */
    @GetMapping("/popular")
    public ResponseEntity<List<LocationResponse>> getPopularLocations(
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        List<LocationResponse> locations = locationService.getPopularLocations(limit);
        return ResponseEntity.ok(locations);
    }

    /**
     * Check location availability for a specific time period.
     *
     * Example: GET
     * /api/locations/{id}/availability?startTime=...&endTime=...&capacity=5
     *
     * @param id        Location ID
     * @param startTime Booking start time (ISO 8601 format)
     * @param endTime   Booking end time (ISO 8601 format)
     * @param capacity  Required capacity
     * @return Boolean indicating availability
     */
    @GetMapping("/{id}/availability")
    public ResponseEntity<Map<String, Boolean>> checkAvailability(
            @PathVariable UUID id,
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam Integer capacity) {
        try {
            Instant start = Instant.parse(startTime);
            Instant end = Instant.parse(endTime);

            boolean available = locationService.isLocationAvailable(id, start, end, capacity);
            return ResponseEntity.ok(Map.of("available", available));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Toggle location active/inactive status.
     * Only the host who owns the location can toggle its status.
     * Uses JWT authentication to identify the host.
     *
     * Example: PATCH /api/locations/{id}/status
     * Header: Authorization: Bearer <jwt-token>
     * Body: { "isActive": false }
     *
     * @param id      Location ID
     * @param payload Contains isActive boolean
     * @return Updated location
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<LocationResponse> toggleLocationStatus(
            @PathVariable UUID id,
            @RequestBody Map<String, Boolean> payload) {
        try {
            UUID hostId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Boolean isActive = payload.get("isActive");
            if (isActive == null) {
                return ResponseEntity.badRequest().build();
            }

            LocationResponse location = locationService.toggleLocationStatus(id, hostId, isActive);
            return ResponseEntity.ok(location);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
