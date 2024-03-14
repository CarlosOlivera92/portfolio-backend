package com.charlesxvr.portfoliobackend.services.imp;

import com.charlesxvr.portfoliobackend.dto.UserInfoDTO;
import com.charlesxvr.portfoliobackend.models.entities.UserInfo;
import com.charlesxvr.portfoliobackend.repositories.UserInfoRepository;
import com.charlesxvr.portfoliobackend.security.models.entities.User;
import com.charlesxvr.portfoliobackend.security.repository.UserRepository;
import com.charlesxvr.portfoliobackend.security.service.imp.UserServiceImp;
import com.charlesxvr.portfoliobackend.services.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImp implements UserInfoService {
    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private UserRepository userRepository;
    public UserInfoDTO getUserInfo(String username) {
        User existingUser = userRepository.findByUsername(username).orElse(null);
        if (existingUser != null) {
            Long userId = existingUser.getId();
            UserInfo userInfo = this.userInfoRepository.findByUser_Id(userId);
            UserInfoDTO userInfoDTO = new UserInfoDTO();
            userInfoDTO.setProfilePicUrl(userInfo.getProfilePicUrl());
            userInfoDTO.setAboutMe(userInfo.getAboutMe());
            userInfoDTO.setJobPosition(userInfo.getJobPosition());
            userInfoDTO.setBannerPicUrl(userInfo.getBannerPicUrl());
            return userInfoDTO;
        } else {
            return null;
        }
    }
    public UserInfoDTO createUserInfo(UserInfo userInfo, String username) {

        User existingUser = userRepository.findByUsername(username).orElse(null);
        if (existingUser != null) {
            userInfo.setUser(existingUser);
            this.userInfoRepository.save(userInfo);
            UserInfoDTO userInfoDTO = new UserInfoDTO();
            userInfoDTO.setAboutMe(userInfo.getAboutMe());
            userInfoDTO.setBannerPicUrl(userInfo.getBannerPicUrl());
            userInfoDTO.setJobPosition(userInfo.getJobPosition());
            userInfoDTO.setProfilePicUrl(userInfo.getProfilePicUrl());
            return userInfoDTO;
        } else {
            return null;
        }

    }

}
