package com.dani.luggagebackend.Controller;

import com.dani.luggagebackend.DTO.BookingResponse;
import com.dani.luggagebackend.Service.HostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller for host-specific operations.
 * Hosts can view bookings at their locations and get dashboard statistics.
 */
@CrossOrigin
@RestController
@RequestMapping("/api/host")
public class  HostController {

    @Autowired
    private HostService hostService;

    /**
     * Gets all bookings across all locations owned by the host.
     * Uses JWT authentication to identify the host.
     *
     * Example request:
     * GET /api/host/bookings
     * Header: Authorization: Bearer <jwt-token>
     *
     * @return List of all bookings across host's locations
     */
    @GetMapping("/bookings")
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        UUID hostId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<BookingResponse> bookings = hostService.getBookingsForHost(hostId);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Gets all bookings for a specific location.
     * Verifies the location belongs to the requesting host.
     * Uses JWT authentication to identify the host.
     *
     * Example request:
     * GET /api/host/locations/{locationId}/bookings
     * Header: Authorization: Bearer <jwt-token>
     *
     * @param locationId The location ID
     * @return List of bookings for the location
     */
    @GetMapping("/locations/{locationId}/bookings")
    public ResponseEntity<List<BookingResponse>> getBookingsForLocation(
            @PathVariable UUID locationId) {
        try {
            UUID hostId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            List<BookingResponse> bookings = hostService.getBookingsForLocation(locationId, hostId);
            return ResponseEntity.ok(bookings);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Gets a dashboard summary with booking statistics.
     * Includes total bookings and breakdowns by status (pending, confirmed, etc.).
     * Uses JWT authentication to identify the host.
     *
     * Example request:
     * GET /api/host/dashboard
     * Header: Authorization: Bearer <jwt-token>
     *
     * Example response:
     * {
     *   "totalBookings": 150,
     *   "pendingBookings": 5,
     *   "confirmedBookings": 130,
     *   "cancelledBookings": 10,
     *   "completedBookings": 5
     * }
     *
     * @return Dashboard statistics
     */
    @GetMapping("/dashboard")
    public ResponseEntity<HostService.BookingDashboard> getDashboard() {
        UUID hostId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        HostService.BookingDashboard dashboard = hostService.getDashboard(hostId);
        return ResponseEntity.ok(dashboard);
    }
}