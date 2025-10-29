package com.dani.luggagebackend.Service;

import com.dani.luggagebackend.DTO.BookingResponse;
import com.dani.luggagebackend.DTO.CreateBookingRequest;
import com.dani.luggagebackend.DTO.UpdateBookingRequest;
import com.dani.luggagebackend.Model.Booking;
import com.dani.luggagebackend.Model.Location;
import com.dani.luggagebackend.Model.Users;
import com.dani.luggagebackend.Repo.BookingRepo;
import com.dani.luggagebackend.Repo.LocationRepo;
import com.dani.luggagebackend.Repo.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingsService {

    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private LocationRepo locationRepo;

    /**
     * Create a new booking
     *
     * @param userId  User making the booking
     * @param request Booking details
     * @return Created booking response
     * @throws RuntimeException if user/location not found or validation fails
     */
    @Transactional
    public BookingResponse createBooking(UUID userId, CreateBookingRequest request) {
        // Validate user exists
        Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate location exists and is active
        Location location = locationRepo.findById(request.getLocationId())
                .orElseThrow(() -> new RuntimeException("Location not found"));

        if (!location.getIsActive()) {
            throw new RuntimeException("Location is not active");
        }

        // Validate time range
        if (request.getEndTime().isBefore(request.getStartTime()) ||
                request.getEndTime().equals(request.getStartTime())) {
            throw new RuntimeException("End time must be after start time");
        }

        // Validate start time is in the future
        if (request.getStartTime().isBefore(Instant.now())) {
            throw new RuntimeException("Start time must be in the future");
        }

        // Calculate price based on duration and location's hourly rate
        Long priceCents = calculatePrice(request.getStartTime(), request.getEndTime(),
                location.getPricePerHour());

        // Create booking
        Booking booking = Booking.builder()
                .user(user)
                .location(location)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .priceCents(priceCents)
                .status(Booking.BookingStatus.PENDING)
                .build();

        Booking savedBooking = bookingRepo.save(booking);

        return convertToResponse(savedBooking);
    }

    /**
     * Get booking by ID
     *
     * @param bookingId Booking UUID
     * @return Booking response if found
     */
    public Optional<BookingResponse> getBookingById(UUID bookingId) {
        return bookingRepo.findById(bookingId)
                .map(this::convertToResponse);
    }

    /**
     * Get all bookings for a user
     *
     * @param userId User UUID
     * @return List of user's bookings
     */
    public List<BookingResponse> getUserBookings(UUID userId) {
        return bookingRepo.findAll().stream()
                .filter(booking -> booking.getUser().getId().equals(userId))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update booking details
     * Only pending bookings can be updated
     * Only the user who created the booking can update it
     *
     * @param bookingId Booking ID
     * @param userId    User ID making the update
     * @param request   Update details
     * @return Updated booking
     * @throws RuntimeException if booking not found, unauthorized, or invalid status
     */
    @Transactional
    public BookingResponse updateBooking(UUID bookingId, UUID userId, UpdateBookingRequest request) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Verify the user owns this booking
        if (!booking.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only update your own bookings");
        }

        // Only pending bookings can be updated
        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new RuntimeException("Only pending bookings can be updated");
        }

        boolean timeChanged = false;

        // Update start time if provided
        if (request.getStartTime() != null) {
            if (request.getStartTime().isBefore(Instant.now())) {
                throw new RuntimeException("Start time must be in the future");
            }
            booking.setStartTime(request.getStartTime());
            timeChanged = true;
        }

        // Update end time if provided
        if (request.getEndTime() != null) {
            booking.setEndTime(request.getEndTime());
            timeChanged = true;
        }

        // Validate time range after updates
        if (booking.getEndTime().isBefore(booking.getStartTime()) ||
                booking.getEndTime().equals(booking.getStartTime())) {
            throw new RuntimeException("End time must be after start time");
        }

        // Recalculate price if time changed
        if (timeChanged) {
            Long newPrice = calculatePrice(booking.getStartTime(), booking.getEndTime(),
                    booking.getLocation().getPricePerHour());
            booking.setPriceCents(newPrice);
        }

        Booking updatedBooking = bookingRepo.save(booking);
        return convertToResponse(updatedBooking);
    }

    /**
     * Cancel a booking
     * Only pending or confirmed bookings can be cancelled
     * User can cancel their own bookings
     *
     * @param bookingId Booking ID
     * @param userId    User ID requesting cancellation
     * @throws RuntimeException if unauthorized or invalid status
     */
    @Transactional
    public void cancelBooking(UUID bookingId, UUID userId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Verify the user owns this booking
        if (!booking.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only cancel your own bookings");
        }

        // Only pending or confirmed bookings can be cancelled
        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking is already cancelled");
        }

        if (booking.getStatus() == Booking.BookingStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel a completed booking");
        }

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        bookingRepo.save(booking);
    }

    /**
     * Confirm a booking (host only)
     * Changes status from PENDING to CONFIRMED
     *
     * @param bookingId Booking ID
     * @param hostId    Host ID confirming the booking
     * @return Updated booking
     * @throws RuntimeException if unauthorized or invalid status
     */
    @Transactional
    public BookingResponse confirmBooking(UUID bookingId, UUID hostId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Verify the host owns the location
        if (!booking.getLocation().getHost().getId().equals(hostId)) {
            throw new RuntimeException("Only the location host can confirm bookings");
        }

        // Only pending bookings can be confirmed
        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new RuntimeException("Only pending bookings can be confirmed");
        }

        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        Booking updatedBooking = bookingRepo.save(booking);

        return convertToResponse(updatedBooking);
    }

    /**
     * Complete a booking (host or system)
     * Changes status to COMPLETED
     * Typically called when the booking end time has passed
     *
     * @param bookingId Booking ID
     * @param hostId    Host ID completing the booking
     * @return Updated booking
     * @throws RuntimeException if unauthorized or invalid status
     */
    @Transactional
    public BookingResponse completeBooking(UUID bookingId, UUID hostId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Verify the host owns the location
        if (!booking.getLocation().getHost().getId().equals(hostId)) {
            throw new RuntimeException("Only the location host can complete bookings");
        }

        // Only confirmed bookings can be completed
        if (booking.getStatus() != Booking.BookingStatus.CONFIRMED) {
            throw new RuntimeException("Only confirmed bookings can be completed");
        }

        booking.setStatus(Booking.BookingStatus.COMPLETED);
        Booking updatedBooking = bookingRepo.save(booking);

        return convertToResponse(updatedBooking);
    }

    /**
     * Get all bookings (admin only - for testing)
     *
     * @return List of all bookings
     */
    public List<BookingResponse> getAllBookings() {
        return bookingRepo.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Calculate price in cents based on duration and hourly rate
     *
     * @param startTime Start time
     * @param endTime   End time
     * @param pricePerHour Price per hour
     * @return Price in cents
     */
    private Long calculatePrice(Instant startTime, Instant endTime, BigDecimal pricePerHour) {
        Duration duration = Duration.between(startTime, endTime);
        double hours = duration.toMinutes() / 60.0;

        // Calculate price: hours * pricePerHour, convert to cents
        BigDecimal price = pricePerHour.multiply(BigDecimal.valueOf(hours));
        return price.multiply(BigDecimal.valueOf(100)).longValue();
    }

    /**
     * Convert Booking entity to BookingResponse DTO
     */
    private BookingResponse convertToResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .user(BookingResponse.UserInfo.builder()
                        .id(booking.getUser().getId())
                        .fullName(booking.getUser().getFullName())
                        .email(booking.getUser().getEmail())
                        .build())
                .location(BookingResponse.LocationInfo.builder()
                        .id(booking.getLocation().getId())
                        .name(booking.getLocation().getName())
                        .address(booking.getLocation().getAddress())
                        .build())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .priceCents(booking.getPriceCents())
                .status(booking.getStatus())
                .build();
    }
}