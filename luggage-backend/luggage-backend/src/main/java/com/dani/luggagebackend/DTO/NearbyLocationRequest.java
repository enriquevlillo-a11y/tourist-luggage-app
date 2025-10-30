package com.dani.luggagebackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NearbyLocationRequest {
    private Double latitude;
    private Double longitude;
    private Double radiusKm = 5.0; // Default radius of 5 km
}