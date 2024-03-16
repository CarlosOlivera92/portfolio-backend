package com.charlesxvr.portfoliobackend.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class ProfessionalBackground {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String jobTitle;
    private String companyName;
    private Date startDate;
    private Date endDate;
    private String summary;

    @ManyToOne(fetch = FetchType.LAZY) @JsonIgnore
    @JoinColumn(name = "userInfo_id")
    private UserInfo userInfo;
}
