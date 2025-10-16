package com.dani.luggagebackend.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;


@Entity
@Component
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Booking {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    private Instant startTime;
    private Instant endTime;
    private Long priceCents;
    private String Status;

}
