package com.charlesxvr.portfoliobackend.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data @Entity
public class Certifications {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String certificationUrl;
    private String degree;
    @ManyToOne(fetch = FetchType.LAZY) @JsonIgnore
    @JoinColumn(name = "educational_background_id")
    private EducationalBackground educationalBackground;
    @ManyToOne(fetch = FetchType.LAZY) @JsonIgnore
    @JoinColumn(name = "courses_id")
    private Courses courses;
}
