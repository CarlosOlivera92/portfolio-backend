package com.charlesxvr.portfoliobackend.dto;

import com.charlesxvr.portfoliobackend.models.entities.*;
import com.charlesxvr.portfoliobackend.security.models.entities.User;
import jakarta.persistence.Lob;
import lombok.Data;

import java.util.List;

@Data
public class UserInfoDTO {
    private Long id;
    private String bannerPicUrl;
    private String jobPosition;
    private String aboutMe;
    private String address;
    private List<EducationalBackground> educationalBackgrounds;
    private List<ProfessionalBackground> professionalBackgrounds;
    private List<Courses> courses;
    private List<Certifications> certifications;
    private List<Projects> projects;

    public UserInfoDTO(UserInfo userInfo) {
        this.id = userInfo.getId();
        this.bannerPicUrl = userInfo.getBannerPicUrl();
        this.jobPosition = userInfo.getJobPosition();
        this.aboutMe = userInfo.getAboutMe();
        this.address = userInfo.getAddress();
        this.educationalBackgrounds = userInfo.getEducationalBackgrounds();
        this.professionalBackgrounds = userInfo.getProfessionalBackgrounds();
        this.courses = userInfo.getCourses();
        this.certifications = userInfo.getCertifications();
        this.projects = userInfo.getProjects();
    }
}
