package com.dani.luggagebackend.Controller;

import com.dani.luggagebackend.DTO.BookingResponse;
import com.dani.luggagebackend.DTO.CreateBookingRequest;
import com.dani.luggagebackend.DTO.UpdateBookingRequest;
import com.dani.luggagebackend.Service.BookingsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller for managing bookings.
 * Handles customer booking operations and lifecycle management.
 */
@CrossOrigin
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingsService service;

    /**
     * Create a new booking
     * Uses JWT authentication to identify the customer.
     *
     * Example request:
     * POST /api/bookings
     * Header: Authorization: Bearer <jwt-token>
     * Body:
     * {
     *   "locationId": "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
     *   "startTime": "2025-02-01T10:00:00Z",
     *   "endTime": "2025-02-01T16:00:00Z",
     *   "numberOfItems": 2
     * }
     *
     * @param request Booking details
     * @return Created booking with calculated price
     */
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody CreateBookingRequest request) {
        try {
            UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            BookingResponse booking = service.createBooking(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get booking by ID
     *
     * Example: GET /api/bookings/{bookingId}
     *
     * @param bookingId Booking UUID
     * @return Booking details if found
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable UUID bookingId) {
        return service.getBookingById(bookingId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all bookings for a user
     *
     * Example: GET /api/bookings/user/{userId}
     *
     * @param userId User UUID
     * @return List of user's bookings
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingResponse>> getUserBookings(@PathVariable UUID userId) {
        List<BookingResponse> bookings = service.getUserBookings(userId);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Get current user's bookings
     * Uses JWT authentication to identify the user.
     *
     * Example: GET /api/bookings/me
     * Header: Authorization: Bearer <jwt-token>
     *
     * @return List of user's bookings
     */
    @GetMapping("/me")
    public ResponseEntity<List<BookingResponse>> getMyBookings() {
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<BookingResponse> bookings = service.getUserBookings(userId);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Get all bookings (admin/testing only)
     *
     * Example: GET /api/bookings
     *
     * NOTE: In production, restrict this to admins only
     *
     * @return List of all bookings
     */
    @GetMapping
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        List<BookingResponse> bookings = service.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    /**
     * Update booking details
     * Only pending bookings can be updated
     * User can only update their own bookings
     * Uses JWT authentication to identify the user.
     *
     * Example request:
     * PUT /api/bookings/{bookingId}
     * Header: Authorization: Bearer <jwt-token>
     * Body:
     * {
     *   "startTime": "2025-02-01T11:00:00Z",
     *   "endTime": "2025-02-01T17:00:00Z"
     * }
     *
     * @param bookingId Booking ID to update
     * @param request   Update details
     * @return Updated booking with recalculated price
     */
    @PutMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> updateBooking(
            @PathVariable UUID bookingId,
            @Valid @RequestBody UpdateBookingRequest request) {
        try {
            UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            BookingResponse booking = service.updateBooking(bookingId, userId, request);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Cancel a booking
     * User can cancel their own pending or confirmed bookings
     * Uses JWT authentication to identify the user.
     *
     * Example: DELETE /api/bookings/{bookingId}
     * Header: Authorization: Bearer <jwt-token>
     *
     * @param bookingId Booking ID to cancel
     * @return Success message
     */
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Map<String, String>> cancelBooking(
            @PathVariable UUID bookingId) {
        try {
            UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            service.cancelBooking(bookingId, userId);
            return ResponseEntity.ok(Map.of("message", "Booking cancelled successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Confirm a booking (host only)
     * Changes status from PENDING to CONFIRMED
     * Uses JWT authentication to identify the host.
     *
     * Example: PATCH /api/bookings/{bookingId}/confirm
     * Header: Authorization: Bearer <jwt-token>
     *
     * @param bookingId Booking ID to confirm
     * @return Updated booking
     */
    @PatchMapping("/{bookingId}/confirm")
    public ResponseEntity<BookingResponse> confirmBooking(
            @PathVariable UUID bookingId) {
        try {
            UUID hostId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            BookingResponse booking = service.confirmBooking(bookingId, hostId);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Complete a booking (host only)
     * Changes status to COMPLETED
     * Usually done after the booking end time has passed
     * Uses JWT authentication to identify the host.
     *
     * Example: PATCH /api/bookings/{bookingId}/complete
     * Header: Authorization: Bearer <jwt-token>
     *
     * @param bookingId Booking ID to complete
     * @return Updated booking
     */
    @PatchMapping("/{bookingId}/complete")
    public ResponseEntity<BookingResponse> completeBooking(
            @PathVariable UUID bookingId) {
        try {
            UUID hostId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            BookingResponse booking = service.completeBooking(bookingId, hostId);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}