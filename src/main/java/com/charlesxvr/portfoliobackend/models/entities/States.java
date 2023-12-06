package com.charlesxvr.portfoliobackend.models.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class States {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String state;
    @OneToOne(mappedBy = "state", fetch = FetchType.LAZY)
    private Townships township;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Countries country;
}
