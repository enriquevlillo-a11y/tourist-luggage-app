package com.dani.luggagebackend.Controller;

import com.dani.luggagebackend.DTO.BookingResponse;
import com.dani.luggagebackend.Service.HostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class HostController {

    @Autowired
    private HostService hostService;

    /**
     * Gets all bookings across all locations owned by the host.
     *
     * NOTE: In production, the hostId should come from the authenticated user's session/token.
     * For now, it's passed as a header for testing purposes.
     *
     * Example request:
     * GET /api/host/bookings
     * Header: X-User-Id: <host-uuid>
     *
     * @param hostId The host's user ID from header
     * @return List of all bookings across host's locations
     */
    @GetMapping("/bookings")
    public ResponseEntity<List<BookingResponse>> getAllBookings(
            @RequestHeader("X-User-Id") UUID hostId) {
        List<BookingResponse> bookings = hostService.getBookingsForHost(hostId);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Gets all bookings for a specific location.
     * Verifies the location belongs to the requesting host.
     *
     * Example request:
     * GET /api/host/locations/{locationId}/bookings
     * Header: X-User-Id: <host-uuid>
     *
     * @param locationId The location ID
     * @param hostId The host's user ID from header
     * @return List of bookings for the location
     */
    @GetMapping("/locations/{locationId}/bookings")
    public ResponseEntity<List<BookingResponse>> getBookingsForLocation(
            @PathVariable UUID locationId,
            @RequestHeader("X-User-Id") UUID hostId) {
        try {
            List<BookingResponse> bookings = hostService.getBookingsForLocation(locationId, hostId);
            return ResponseEntity.ok(bookings);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Gets a dashboard summary with booking statistics.
     * Includes total bookings and breakdowns by status (pending, confirmed, etc.).
     *
     * Example request:
     * GET /api/host/dashboard
     * Header: X-User-Id: <host-uuid>
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
     * @param hostId The host's user ID from header
     * @return Dashboard statistics
     */
    @GetMapping("/dashboard")
    public ResponseEntity<HostService.BookingDashboard> getDashboard(
            @RequestHeader("X-User-Id") UUID hostId) {
        HostService.BookingDashboard dashboard = hostService.getDashboard(hostId);
        return ResponseEntity.ok(dashboard);
    }
}