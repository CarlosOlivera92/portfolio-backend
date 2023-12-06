package com.charlesxvr.portfoliobackend.models.entities;

import jakarta.persistence.*;
import lombok.Data;

import javax.swing.plaf.nimbus.State;

@Entity
@Data
public class Townships {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String township;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id")
    private States state;
    @OneToOne(mappedBy = "township", fetch = FetchType.LAZY)
    private UserInfo userInfo;
}
