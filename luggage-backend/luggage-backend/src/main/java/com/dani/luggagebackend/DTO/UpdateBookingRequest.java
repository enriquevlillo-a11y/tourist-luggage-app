package com.dani.luggagebackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateBookingRequest {

    // All fields are optional for updates
    private Instant startTime;

    private Instant endTime;

    private Integer numberOfItems;
}