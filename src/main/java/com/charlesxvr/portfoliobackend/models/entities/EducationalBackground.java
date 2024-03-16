package com.charlesxvr.portfoliobackend.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class EducationalBackground {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String institution;
    private String degree;
    private Date startDate;
    private Date endDate;
    private String focusOfStudies;
    @ManyToOne(fetch = FetchType.LAZY) @JsonIgnore
    @JoinColumn(name = "userInfo_id")
    private UserInfo userInfo;
}
