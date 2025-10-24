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
    private Double latitude;
    private Double longitude;
    private BigDecimal pricePerHour;
    private Integer capacity;
    private String hours;
    private Double distanceKm; // Distance from user's location
    private HostInfo host; // Information about the host

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class HostInfo {
        private UUID id;
        private String fullName;
        private String email;
    }
}
