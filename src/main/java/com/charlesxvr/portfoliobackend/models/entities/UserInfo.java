package com.charlesxvr.portfoliobackend.models.entities;

import com.charlesxvr.portfoliobackend.security.models.entities.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.Base64;
import java.util.List;

@Entity
@Data
@ToString(exclude = {"user", "educationalBackgrounds", "professionalBackgrounds", "courses", "certifications", "projects"})

public class UserInfo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Lob
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
    @OneToMany(mappedBy = "userInfo", cascade = CascadeType.ALL)
    private List<EducationalBackground> educationalBackgrounds;

    @OneToMany(mappedBy = "userInfo", cascade = CascadeType.ALL)
    private List<ProfessionalBackground> professionalBackgrounds;

    @OneToMany(mappedBy = "userInfo", cascade = CascadeType.ALL)
    private List<Courses> courses;

    @OneToMany(mappedBy = "userInfo", cascade = CascadeType.ALL)
    private List<Certifications> certifications;

    @OneToMany(mappedBy = "userInfo", cascade = CascadeType.ALL)
    private List<Projects> projects;
}
