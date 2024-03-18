package com.charlesxvr.portfoliobackend.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data @Entity
public class Projects {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String projectName;
    private String projectUrl;
    private String projectRepoUrl;
    private String summary;
    @ManyToOne(fetch = FetchType.LAZY) @JsonIgnore
    @JoinColumn(name = "userInfo_id")
    private UserInfo userInfo;
}
