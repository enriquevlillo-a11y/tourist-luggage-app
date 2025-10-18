package com.dani.luggagebackend.Model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@Entity
@Table(name = "locations")
public class Location {
    @Id
    private UUID id;

    private String name;
    private String address;
    private Double lat;
    private Double lng;
    private BigDecimal pricePerHour;
    private Integer capacity;

    private String hours;
}
