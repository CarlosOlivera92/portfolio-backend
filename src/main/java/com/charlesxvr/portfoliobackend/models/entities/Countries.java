package com.charlesxvr.portfoliobackend.models.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Countries {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String country;
    @OneToOne(mappedBy = "country", fetch = FetchType.LAZY)
    private States state;
}
