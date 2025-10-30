package com.dani.luggagebackend.Service;

import com.dani.luggagebackend.DTO.BookingResponse;
import com.dani.luggagebackend.Model.Booking;
import com.dani.luggagebackend.Model.Location;
import com.dani.luggagebackend.Repo.BookingRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class HostService {

    @Autowired
    private BookingRepo bookingRepo;

    /**
     * Gets all bookings for a host's locations.
     *
     * @param hostId Host's user ID
     * @return List of all bookings across all host's locations
     */
    public List<BookingResponse> getBookingsForHost(UUID hostId) {
        List<Booking> bookings = bookingRepo.findByLocationHostId(hostId);
        return bookings.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Gets all bookings for a specific location.
     * Verifies that the location belongs to the requesting host.
     *
     * @param locationId Location ID
     * @param hostId Host's user ID
     * @return List of bookings for the location
     * @throws RuntimeException if location doesn't belong to the host
     */
    public List<BookingResponse> getBookingsForLocation(UUID locationId, UUID hostId) {
        List<Booking> bookings = bookingRepo.findByLocationId(locationId);

        // Verify the first booking's location belongs to this host
        // (if there are no bookings, return empty list)
        if (!bookings.isEmpty()) {
            Location location = bookings.get(0).getLocation();
            if (!location.getHost().getId().equals(hostId)) {
                throw new RuntimeException("This location does not belong to you");
            }
        }

        return bookings.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Gets a booking dashboard summary for a host.
     * Includes counts by status across all their locations.
     *
     * @param hostId Host's user ID
     * @return Dashboard statistics
     */
    public BookingDashboard getDashboard(UUID hostId) {
        List<Booking> bookings = bookingRepo.findByLocationHostId(hostId);

        long pending = bookings.stream()
                .filter(b -> b.getStatus() == Booking.BookingStatus.PENDING)
                .count();
        long confirmed = bookings.stream()
                .filter(b -> b.getStatus() == Booking.BookingStatus.CONFIRMED)
                .count();
        long cancelled = bookings.stream()
                .filter(b -> b.getStatus() == Booking.BookingStatus.CANCELLED)
                .count();
        long completed = bookings.stream()
                .filter(b -> b.getStatus() == Booking.BookingStatus.COMPLETED)
                .count();

        return new BookingDashboard(bookings.size(), pending, confirmed, cancelled, completed);
    }

    /**
     * Converts a Booking entity to BookingResponse DTO.
     */
    private BookingResponse convertToResponse(Booking booking) {
        BookingResponse.UserInfo userInfo = BookingResponse.UserInfo.builder()
                .id(booking.getUser().getId())
                .fullName(booking.getUser().getFullName())
                .email(booking.getUser().getEmail())
                .build();

        BookingResponse.LocationInfo locationInfo = BookingResponse.LocationInfo.builder()
                .id(booking.getLocation().getId())
                .name(booking.getLocation().getName())
                .address(booking.getLocation().getAddress())
                .build();

        return BookingResponse.builder()
                .id(booking.getId())
                .user(userInfo)
                .location(locationInfo)
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .priceCents(booking.getPriceCents())
                .status(booking.getStatus())
                .build();
    }

    /**
     * Dashboard statistics for a host.
     */
    public record BookingDashboard(
            long totalBookings,
            long pendingBookings,
            long confirmedBookings,
            long cancelledBookings,
            long completedBookings
    ) {}
}