package com.charlesxvr.portfoliobackend.services;

import com.charlesxvr.portfoliobackend.dto.UserInfoDTO;
import com.charlesxvr.portfoliobackend.models.entities.UserInfo;
import org.springframework.stereotype.Service;

@Service
public interface UserInfoService {
    UserInfo getUserInfo(String username);
    UserInfoDTO createUserInfo(UserInfo userInfo, String username);
    UserInfo updateUserInfo(UserInfo userInfo, String username);
}
