package com.charlesxvr.portfoliobackend.dto;

import lombok.Data;

@Data
public class UserInfoDTO {
    private String profilePicUrl;
    private String bannerPicUrl;
    private String jobPosition;
    private String aboutMe;
}