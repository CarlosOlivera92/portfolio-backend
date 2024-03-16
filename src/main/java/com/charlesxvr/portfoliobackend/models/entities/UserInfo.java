package com.charlesxvr.portfoliobackend.models.entities;

import com.charlesxvr.portfoliobackend.security.models.entities.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class UserInfo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String profilePicUrl;
    private String bannerPicUrl;
    private String jobPosition;
    private String aboutMe;
    private String linkedinProfileUrl;
    private String githubProfileUrl;
    @Nullable
    private String address;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id")
    private States state;
    @OneToOne(fetch = FetchType.LAZY)@JsonIgnore
    @JoinColumn(name = "users_id")
    private User user;
}
