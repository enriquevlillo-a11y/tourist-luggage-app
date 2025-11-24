package com.dani.luggagebackend.Service;

import com.dani.luggagebackend.DTO.CreateLocationRequest;
import com.dani.luggagebackend.DTO.LocationResponse;
import com.dani.luggagebackend.Exception.BadRequestException;
import com.dani.luggagebackend.Exception.ForbiddenException;
import com.dani.luggagebackend.Exception.ResourceNotFoundException;
import com.dani.luggagebackend.Model.Booking;
import com.dani.luggagebackend.Model.Location;
import com.dani.luggagebackend.Model.Users;
import com.dani.luggagebackend.Repo.BookingRepo;
import com.dani.luggagebackend.Repo.LocationRepo;
import com.dani.luggagebackend.Repo.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
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

    @Autowired
    private BookingRepo bookingRepo;

    /**
     * Finds all storage locations within a specified radius of the user's current
     * position.
     * Results are ordered by distance (closest first).
     *
     * @param latitude  User's current latitude
     * @param longitude User's current longitude
     * @param radiusKm  Search radius in kilometers
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
     * Gets all available storage locations with pagination.
     *
     * @param pageable Pagination information (page number, size, sort)
     * @return Page of locations
     */
    public Page<LocationResponse> getAllLocations(Pageable pageable) {
        return locationRepo.findAll(pageable)
                .map(location -> convertToResponse(location, null, null));
    }

    /**
     * Creates a new location for a host.
     *
     * @param hostId  Host's user ID
     * @param request Location creation request
     * @return Created LocationResponse
     * @throws RuntimeException if host not found or not a HOST role
     */
    @Transactional
    public LocationResponse createLocation(UUID hostId, CreateLocationRequest request) {
        Users host = usersRepo.findById(hostId)
                .orElseThrow(() -> new ResourceNotFoundException("Host not found"));

        if (host.getRole() != Users.Role.HOST) {
            throw new BadRequestException("User is not a host");
        }

        Location location = Location.builder()
                .host(host)
                .name(request.getName())
                .address(request.getAddress())
                .city(request.getCity())
                .lat(request.getLatitude())
                .lng(request.getLongitude())
                .pricePerHour(request.getPricePerHour())
                .capacity(request.getCapacity())
                .hours(request.getHours())
                .isActive(true)
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
     * @param hostId     Host ID making the request
     * @param request    Update request
     * @return Updated LocationResponse
     * @throws RuntimeException if location not found or host doesn't own it
     */
    @Transactional
    public LocationResponse updateLocation(UUID locationId, UUID hostId, CreateLocationRequest request) {
        Location location = locationRepo.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found"));

        if (!location.getHost().getId().equals(hostId)) {
            throw new ForbiddenException("You don't have permission to update this location");
        }

        location.setName(request.getName());
        location.setAddress(request.getAddress());
        location.setCity(request.getCity());
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
     * @param hostId     Host ID making the request
     * @throws RuntimeException if location not found or host doesn't own it
     */
    @Transactional
    public void deleteLocation(UUID locationId, UUID hostId) {
        Location location = locationRepo.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found"));

        if (!location.getHost().getId().equals(hostId)) {
            throw new ForbiddenException("You don't have permission to delete this location");
        }

        locationRepo.delete(location);
    }

    /**
     * Search locations by keyword (searches name and address).
     *
     * @param keyword Search term
     * @return List of matching locations
     */
    public List<LocationResponse> searchLocations(String keyword) {
        List<Location> locations = locationRepo
                .findByIsActiveTrueAndNameContainingIgnoreCaseOrAddressContainingIgnoreCase(keyword, keyword);
        return locations.stream()
                .map(location -> convertToResponse(location, null, null))
                .collect(Collectors.toList());
    }

    /**
     * Filter locations by price range.
     *
     * @param minPrice Minimum price
     * @param maxPrice Maximum price
     * @return List of locations in price range
     */
    public List<LocationResponse> filterByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        List<Location> locations = locationRepo.findByPricePerHourBetween(minPrice, maxPrice);
        return locations.stream()
                .filter(Location::getIsActive)
                .map(location -> convertToResponse(location, null, null))
                .collect(Collectors.toList());
    }

    /**
     * Filter locations by minimum capacity.
     *
     * @param minCapacity Minimum capacity
     * @return List of locations with at least minCapacity
     */
    public List<LocationResponse> filterByCapacity(Integer minCapacity) {
        List<Location> locations = locationRepo.findByCapacityGreaterThanEqual(minCapacity);
        return locations.stream()
                .filter(Location::getIsActive)
                .map(location -> convertToResponse(location, null, null))
                .collect(Collectors.toList());
    }

    /**
     * Get all locations in a specific city.
     *
     * @param city City name
     * @return List of locations in the city
     */
    public List<LocationResponse> getLocationsByCity(String city) {
        List<Location> locations = locationRepo.findByIsActiveTrueAndCityIgnoreCase(city);
        return locations.stream()
                .map(location -> convertToResponse(location, null, null))
                .collect(Collectors.toList());
    }

    /**
     * Get all unique cities with locations.
     *
     * @return List of city names
     */
    public List<String> getAllCities() {
        return locationRepo.findAllCities();
    }

    /**
     * Get popular locations based on booking count.
     *
     * @param limit Maximum number of locations to return
     * @return List of popular locations
     */
    public List<LocationResponse> getPopularLocations(Integer limit) {
        List<Location> locations = locationRepo.findMostPopularLocations();
        return locations.stream()
                .limit(limit != null ? limit : 10)
                .map(location -> convertToResponse(location, null, null))
                .collect(Collectors.toList());
    }

    /**
     * Find nearby locations with filters.
     *
     * @param latitude    User's latitude
     * @param longitude   User's longitude
     * @param radiusKm    Search radius
     * @param minPrice    Minimum price filter (optional)
     * @param maxPrice    Maximum price filter (optional)
     * @param minCapacity Minimum capacity filter (optional)
     * @return List of filtered nearby locations
     */
    public List<LocationResponse> findNearbyWithFilters(
            Double latitude, Double longitude, Double radiusKm,
            BigDecimal minPrice, BigDecimal maxPrice, Integer minCapacity) {
        List<Location> locations = locationRepo.findActiveLocationsWithinRadiusAndFilters(
                latitude, longitude, radiusKm, minPrice, maxPrice, minCapacity);
        return locations.stream()
                .map(location -> convertToResponse(location, latitude, longitude))
                .collect(Collectors.toList());
    }

    /**
     * Check if location has available capacity for a time period.
     *
     * @param locationId       Location ID
     * @param startTime        Booking start time
     * @param endTime          Booking end time
     * @param requiredCapacity Required capacity
     * @return true if location has availability
     */
    @Transactional(readOnly = true)
    public boolean isLocationAvailable(UUID locationId, Instant startTime, Instant endTime, Integer requiredCapacity) {
        Location location = locationRepo.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found"));

        if (!location.getIsActive()) {
            return false;
        }

        // Get overlapping bookings
        List<Booking> bookings = bookingRepo.findByLocationId(locationId);
        long occupiedCapacity = bookings.stream()
                .filter(b -> b.getStatus() == Booking.BookingStatus.CONFIRMED ||
                        b.getStatus() == Booking.BookingStatus.PENDING)
                .filter(b -> {
                    // Check for time overlap
                    return !(b.getEndTime().isBefore(startTime) || b.getStartTime().isAfter(endTime));
                })
                .count();

        return (location.getCapacity() - occupiedCapacity) >= requiredCapacity;
    }

    /**
     * Toggle location active status. Only host can toggle.
     *
     * @param locationId Location ID
     * @param hostId     Host ID
     * @param isActive   New status
     * @return Updated location
     */
    @Transactional
    public LocationResponse toggleLocationStatus(UUID locationId, UUID hostId, Boolean isActive) {
        Location location = locationRepo.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found"));

        if (!location.getHost().getId().equals(hostId)) {
            throw new ForbiddenException("You don't have permission to update this location");
        }

        location.setIsActive(isActive);
        Location updatedLocation = locationRepo.save(location);
        return convertToResponse(updatedLocation, null, null);
    }

    /**
     * Converts Location entity to LocationResponse DTO.
     * If user coordinates are provided, calculates the distance.
     *
     * @param location The location entity
     * @param userLat  User's latitude (nullable)
     * @param userLng  User's longitude (nullable)
     * @return LocationResponse DTO
     */
    @Transactional(readOnly = true)
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
                .city(location.getCity())
                .latitude(location.getLat())
                .longitude(location.getLng())
                .pricePerHour(location.getPricePerHour())
                .capacity(location.getCapacity())
                .hours(location.getHours())
                .isActive(location.getIsActive())
                .distanceKm(distance)
                .host(hostInfo)
                .rating(0.0)
                .reviews(java.util.Collections.emptyList())
                .build();
    }

    /**
     * Calculates the distance between two points on Earth using the Haversine
     * formula.
     * This gives the great-circle distance (shortest distance on the surface of a
     * sphere).
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
