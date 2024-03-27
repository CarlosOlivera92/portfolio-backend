package com.charlesxvr.portfoliobackend.services;

import com.charlesxvr.portfoliobackend.dto.UserInfoDTO;
import com.charlesxvr.portfoliobackend.models.entities.UserInfo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

@Service
public interface UserInfoService {
    UserInfo getUserInfo(String username);
    UserInfoDTO createUserInfo(UserInfo userInfo, String username);
    UserInfo updateUserInfo(UserInfo userInfo, String username);
    String getProfilePic (String username);
    String updateProfilePic(MultipartFile file, String username);
}
