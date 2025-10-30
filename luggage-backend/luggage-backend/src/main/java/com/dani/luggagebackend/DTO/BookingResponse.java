package com.dani.luggagebackend.DTO;

import com.dani.luggagebackend.Model.Booking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingResponse {
    private UUID id;
    private UserInfo user;
    private LocationInfo location;
    private Instant startTime;
    private Instant endTime;
    private Long priceCents;
    private Booking.BookingStatus status;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UserInfo {
        private UUID id;
        private String fullName;
        private String email;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class LocationInfo {
        private UUID id;
        private String name;
        private String address;
    }
}