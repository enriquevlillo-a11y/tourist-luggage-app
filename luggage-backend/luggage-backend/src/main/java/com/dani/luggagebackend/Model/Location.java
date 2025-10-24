package com.dani.luggagebackend.Model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Component
@Entity
@Table(name = "locations")
public class Location {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private Users host;

    private String name;
    private String address;
    private Double lat;
    private Double lng;
    private BigDecimal pricePerHour;
    private Integer capacity;

    private String hours;
}
