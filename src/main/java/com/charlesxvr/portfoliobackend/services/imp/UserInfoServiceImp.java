package com.charlesxvr.portfoliobackend.services.imp;

import com.charlesxvr.portfoliobackend.dto.UserInfoDTO;
import com.charlesxvr.portfoliobackend.models.entities.UserInfo;
import com.charlesxvr.portfoliobackend.repositories.UserInfoRepository;
import com.charlesxvr.portfoliobackend.security.models.entities.User;
import com.charlesxvr.portfoliobackend.security.repository.UserRepository;
import com.charlesxvr.portfoliobackend.security.service.imp.UserServiceImp;
import com.charlesxvr.portfoliobackend.services.UserInfoService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserInfoServiceImp implements UserInfoService {
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserInfo getUserInfo(String username) {
        User existingUser = userRepository.findByUsername(username).orElse(null);
        if (existingUser != null) {
            Long userId = existingUser.getId();
            return this.userInfoRepository.findByUser_Id(userId);
        } else {
            return null;
        }
    }
    @Override
    public UserInfoDTO createUserInfo(UserInfo userInfo, String username) {

        User existingUser = userRepository.findByUsername(username).orElse(null);
        if (existingUser != null) {
            userInfo.setUser(existingUser);
            this.userInfoRepository.save(userInfo);
            UserInfoDTO userInfoDTO = new UserInfoDTO();
            userInfoDTO.setId(userInfoDTO.getId());
            userInfoDTO.setAboutMe(userInfo.getAboutMe());
            userInfoDTO.setBannerPicUrl(userInfo.getBannerPicUrl());
            userInfoDTO.setJobPosition(userInfo.getJobPosition());
            userInfoDTO.setProfilePicUrl(userInfo.getProfilePicUrl());
            return userInfoDTO;
        } else {
            return null;
        }
    }
    @Override
    @Transactional
    public UserInfo updateUserInfo(UserInfo userInfo, String username) {
        try {
            Optional<User> existingUser = this.userRepository.findByUsername(username);
            if (existingUser.isEmpty()) {
                throw new RuntimeException("User not found for username: " + username);
            }

            UserInfo existingUserInfo = existingUser.get().getUserInfo();
            if (existingUserInfo == null) {
                throw new RuntimeException("UserInfo not found for user: " + username);
            }

            // Selectively update fields based on request data, preserving existing values for missing fields
            if (userInfo.getBannerPicUrl() != null) {
                existingUserInfo.setBannerPicUrl(userInfo.getBannerPicUrl());
            }
            if (userInfo.getProfilePicUrl() != null) {
                existingUserInfo.setProfilePicUrl(userInfo.getProfilePicUrl());
            }
            if (userInfo.getJobPosition() != null) {
                existingUserInfo.setJobPosition(userInfo.getJobPosition());
            }
            if (userInfo.getAboutMe() != null) {
                existingUserInfo.setAboutMe(userInfo.getAboutMe());
            }
            if (userInfo.getAddress() != null) {
                existingUserInfo.setAddress(userInfo.getAddress());
            }
            if (userInfo.getGithubProfileUrl() != null) {
                existingUserInfo.setGithubProfileUrl(userInfo.getGithubProfileUrl());
            }
            if (userInfo.getLinkedinProfileUrl() != null) {
                existingUserInfo.setLinkedinProfileUrl(userInfo.getLinkedinProfileUrl());
            }

            return this.userInfoRepository.save(existingUserInfo);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while updating user info data: ", e);
        }
    }

}
