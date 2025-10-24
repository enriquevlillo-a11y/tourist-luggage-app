package com.dani.luggagebackend.Service;

import com.dani.luggagebackend.DTO.CreateLocationRequest;
import com.dani.luggagebackend.DTO.LocationResponse;
import com.dani.luggagebackend.Model.Location;
import com.dani.luggagebackend.Model.Users;
import com.dani.luggagebackend.Repo.LocationRepo;
import com.dani.luggagebackend.Repo.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LocationService {

    @Autowired
    private LocationRepo locationRepo;

    @Autowired
    private UsersRepo usersRepo;

    /**
     * Finds all storage locations within a specified radius of the user's current position.
     * Results are ordered by distance (closest first).
     *
     * @param latitude User's current latitude
     * @param longitude User's current longitude
     * @param radiusKm Search radius in kilometers
     * @return List of LocationResponse DTOs with calculated distances
     */
    public List<LocationResponse> findNearbyLocations(Double latitude, Double longitude, Double radiusKm) {
        List<Location> locations = locationRepo.findLocationsWithinRadius(latitude, longitude, radiusKm);

        return locations.stream()
                .map(location -> convertToResponse(location, latitude, longitude))
                .collect(Collectors.toList());
    }

    /**
     * Gets a specific location by ID.
     *
     * @param id Location UUID
     * @return LocationResponse if found
     */
    public Optional<LocationResponse> getLocationById(UUID id) {
        return locationRepo.findById(id)
                .map(location -> convertToResponse(location, null, null));
    }

    /**
     * Gets all available storage locations.
     *
     * @return List of all locations
     */
    public List<LocationResponse> getAllLocations() {
        return locationRepo.findAll().stream()
                .map(location -> convertToResponse(location, null, null))
                .collect(Collectors.toList());
    }

    /**
     * Creates a new location for a host.
     *
     * @param hostId Host's user ID
     * @param request Location creation request
     * @return Created LocationResponse
     * @throws RuntimeException if host not found or not a HOST role
     */
    public LocationResponse createLocation(UUID hostId, CreateLocationRequest request) {
        Users host = usersRepo.findById(hostId)
                .orElseThrow(() -> new RuntimeException("Host not found"));

        if (host.getRole() != Users.Role.HOST) {
            throw new RuntimeException("User is not a host");
        }

        Location location = Location.builder()
                .host(host)
                .name(request.getName())
                .address(request.getAddress())
                .lat(request.getLatitude())
                .lng(request.getLongitude())
                .pricePerHour(request.getPricePerHour())
                .capacity(request.getCapacity())
                .hours(request.getHours())
                .build();

        Location savedLocation = locationRepo.save(location);
        return convertToResponse(savedLocation, null, null);
    }

    /**
     * Gets all locations owned by a specific host.
     *
     * @param hostId Host's user ID
     * @return List of locations owned by the host
     */
    public List<LocationResponse> getLocationsByHost(UUID hostId) {
        List<Location> locations = locationRepo.findByHostId(hostId);
        return locations.stream()
                .map(location -> convertToResponse(location, null, null))
                .collect(Collectors.toList());
    }

    /**
     * Updates a location. Only the host who owns it can update.
     *
     * @param locationId Location ID to update
     * @param hostId Host ID making the request
     * @param request Update request
     * @return Updated LocationResponse
     * @throws RuntimeException if location not found or host doesn't own it
     */
    public LocationResponse updateLocation(UUID locationId, UUID hostId, CreateLocationRequest request) {
        Location location = locationRepo.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found"));

        if (!location.getHost().getId().equals(hostId)) {
            throw new RuntimeException("You don't have permission to update this location");
        }

        location.setName(request.getName());
        location.setAddress(request.getAddress());
        location.setLat(request.getLatitude());
        location.setLng(request.getLongitude());
        location.setPricePerHour(request.getPricePerHour());
        location.setCapacity(request.getCapacity());
        location.setHours(request.getHours());

        Location updatedLocation = locationRepo.save(location);
        return convertToResponse(updatedLocation, null, null);
    }

    /**
     * Deletes a location. Only the host who owns it can delete.
     *
     * @param locationId Location ID to delete
     * @param hostId Host ID making the request
     * @throws RuntimeException if location not found or host doesn't own it
     */
    public void deleteLocation(UUID locationId, UUID hostId) {
        Location location = locationRepo.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found"));

        if (!location.getHost().getId().equals(hostId)) {
            throw new RuntimeException("You don't have permission to delete this location");
        }

        locationRepo.delete(location);
    }

    /**
     * Converts Location entity to LocationResponse DTO.
     * If user coordinates are provided, calculates the distance.
     *
     * @param location The location entity
     * @param userLat User's latitude (nullable)
     * @param userLng User's longitude (nullable)
     * @return LocationResponse DTO
     */
    private LocationResponse convertToResponse(Location location, Double userLat, Double userLng) {
        Double distance = null;
        if (userLat != null && userLng != null) {
            distance = calculateDistance(userLat, userLng, location.getLat(), location.getLng());
        }

        LocationResponse.HostInfo hostInfo = null;
        if (location.getHost() != null) {
            hostInfo = LocationResponse.HostInfo.builder()
                    .id(location.getHost().getId())
                    .fullName(location.getHost().getFullName())
                    .email(location.getHost().getEmail())
                    .build();
        }

        return LocationResponse.builder()
                .id(location.getId())
                .name(location.getName())
                .address(location.getAddress())
                .latitude(location.getLat())
                .longitude(location.getLng())
                .pricePerHour(location.getPricePerHour())
                .capacity(location.getCapacity())
                .hours(location.getHours())
                .distanceKm(distance)
                .host(hostInfo)
                .build();
    }

    /**
     * Calculates the distance between two points on Earth using the Haversine formula.
     * This gives the great-circle distance (shortest distance on the surface of a sphere).
     *
     * @param lat1 Latitude of first point
     * @param lng1 Longitude of first point
     * @param lat2 Latitude of second point
     * @param lng2 Longitude of second point
     * @return Distance in kilometers
     */
    private Double calculateDistance(Double lat1, Double lng1, Double lat2, Double lng2) {
        final int EARTH_RADIUS_KM = 6371;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lngDistance = Math.toRadians(lng2 - lng1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
}
