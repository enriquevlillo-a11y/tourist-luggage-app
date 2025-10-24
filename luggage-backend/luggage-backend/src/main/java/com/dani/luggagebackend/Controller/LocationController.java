package com.dani.luggagebackend.Controller;

import com.dani.luggagebackend.DTO.CreateLocationRequest;
import com.dani.luggagebackend.DTO.LocationResponse;
import com.dani.luggagebackend.DTO.NearbyLocationRequest;
import com.dani.luggagebackend.Service.LocationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
     *   "latitude": 40.7128,
     *   "longitude": -74.0060,
     *   "radiusKm": 10.0
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
                radius
        );

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
     * Gets all available storage locations.
     * Useful for admins or displaying all locations on a map.
     *
     * @return List of all locations
     */
    @GetMapping
    public ResponseEntity<List<LocationResponse>> getAllLocations() {
        List<LocationResponse> locations = locationService.getAllLocations();
        return ResponseEntity.ok(locations);
    }

    /**
     * Creates a new location. Only hosts can create locations.
     *
     * NOTE: In production, the hostId should come from the authenticated user's session/token.
     * For now, it's passed as a header for testing purposes.
     *
     * Example request:
     * Header: X-User-Id: <host-uuid>
     * Body:
     * {
     *   "name": "Downtown Luggage Storage",
     *   "address": "123 Main St, New York, NY",
     *   "latitude": 40.7128,
     *   "longitude": -74.0060,
     *   "pricePerHour": 5.00,
     *   "capacity": 50,
     *   "hours": "Mon-Fri: 9AM-6PM"
     * }
     *
     * @param hostId The host's user ID from header
     * @param request Location creation request
     * @return Created location with HTTP 201
     */
    @PostMapping
    public ResponseEntity<LocationResponse> createLocation(
            @RequestHeader("X-User-Id") UUID hostId,
            @Valid @RequestBody CreateLocationRequest request) {
        try {
            LocationResponse location = locationService.createLocation(hostId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(location);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Updates an existing location. Only the host who owns it can update.
     *
     * NOTE: In production, the hostId should come from the authenticated user's session/token.
     *
     * @param id Location ID to update
     * @param hostId The host's user ID from header
     * @param request Update request
     * @return Updated location
     */
    @PutMapping("/{id}")
    public ResponseEntity<LocationResponse> updateLocation(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID hostId,
            @Valid @RequestBody CreateLocationRequest request) {
        try {
            LocationResponse location = locationService.updateLocation(id, hostId, request);
            return ResponseEntity.ok(location);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Deletes a location. Only the host who owns it can delete.
     *
     * NOTE: In production, the hostId should come from the authenticated user's session/token.
     *
     * @param id Location ID to delete
     * @param hostId The host's user ID from header
     * @return HTTP 204 on success
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID hostId) {
        try {
            locationService.deleteLocation(id, hostId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Gets all locations owned by a specific host.
     *
     * NOTE: In production, the hostId should come from the authenticated user's session/token.
     *
     * @param hostId The host's user ID
     * @return List of locations owned by the host
     */
    @GetMapping("/host/{hostId}")
    public ResponseEntity<List<LocationResponse>> getLocationsByHost(@PathVariable UUID hostId) {
        List<LocationResponse> locations = locationService.getLocationsByHost(hostId);
        return ResponseEntity.ok(locations);
    }
}
