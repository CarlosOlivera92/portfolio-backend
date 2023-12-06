package com.charlesxvr.portfoliobackend.models.entities;

import com.charlesxvr.portfoliobackend.security.models.entities.User;
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

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "users_id")
    private User user;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "townships_id")
    private Townships township;
}
