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
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id")
    private Countries country;
    @OneToOne(mappedBy = "states", fetch = FetchType.LAZY)
    private UserInfo userInfo;
}
