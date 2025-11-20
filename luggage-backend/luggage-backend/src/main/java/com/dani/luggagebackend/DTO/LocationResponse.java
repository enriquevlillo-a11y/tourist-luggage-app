package com.dani.luggagebackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationResponse {
    private UUID id;
    private String name;
    private String address;
    private String city;
    private Double latitude;
    private Double longitude;
    private BigDecimal pricePerHour;
    private Integer capacity;
    private String hours;
    private Boolean isActive;
    private Double distanceKm; // Distance from user's location
    private HostInfo host; // Information about the host
    private Double rating;
    private java.util.List<ReviewResponse> reviews;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class HostInfo {
        private UUID id;
        private String fullName;
        private String email;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ReviewResponse {
        private String id;
        private String user;
        private String comment;
        private Integer rating;
        private String createdAt;
    }
}
