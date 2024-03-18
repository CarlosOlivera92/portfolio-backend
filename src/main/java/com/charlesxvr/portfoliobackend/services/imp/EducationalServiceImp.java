package com.charlesxvr.portfoliobackend.services.imp;

import com.charlesxvr.portfoliobackend.models.entities.EducationalBackground;
import com.charlesxvr.portfoliobackend.models.entities.UserInfo;
import com.charlesxvr.portfoliobackend.repositories.EducationalRepository;
import com.charlesxvr.portfoliobackend.repositories.UserInfoRepository;
import com.charlesxvr.portfoliobackend.security.models.entities.User;
import com.charlesxvr.portfoliobackend.security.repository.UserRepository;
import com.charlesxvr.portfoliobackend.services.EducationalService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EducationalServiceImp implements EducationalService {
    @Autowired
    private EducationalRepository educationalRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private UserRepository userRepository;
    @Override
    @Transactional
    public EducationalBackground createEducationalBackground(EducationalBackground educationalBackground, Long userId) {
        try {
            UserInfo userInfo = this.userInfoRepository.findByUser_Id(userId);
            if (userInfo == null) {
                throw new RuntimeException("UserInfo not found for user ID: " + userId);
            }
            educationalBackground.setUserInfo(userInfo);
            return this.educationalRepository.save(educationalBackground);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error creating educational background: " + e.getMessage());
        }
    }

    @Override
    public EducationalBackground getUserEducationalBackgroundById(Long eduId, String username) {
        try {
            Optional<User> existingUser = this.userRepository.findByUsername(username);
            if (existingUser.isEmpty()) {
                throw new RuntimeException("User not found for username: " + username);
            }
            Long userInfoId = existingUser.get().getUserInfo().getId();

            List<EducationalBackground> educationalBackgroundList = this.educationalRepository.findAllByUserInfoId(userInfoId);

            return educationalBackgroundList.stream()
                    .filter(item -> item.getId().equals(eduId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Educational item not found for ID: " + eduId));
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error fetching educational background: ", e);
        }
    }
    @Override
    @Transactional
    public EducationalBackground updateEducationalBackground(EducationalBackground educationalBackground, Long eduId, String username) {
        try {
            Optional<User> existingUser = this.userRepository.findByUsername(username);
            if(existingUser.isEmpty()) {
                throw new RuntimeException("User not found for username: " + username);
            }
            Long userInfoId = existingUser.get().getUserInfo().getId();
            if (userInfoId == null) {
                throw new RuntimeException("UserInfo not found for user: " + username);
            }
            List<EducationalBackground> educationalBackgroundList = this.educationalRepository.findAllByUserInfoId(userInfoId);
            EducationalBackground existingBackground = educationalBackgroundList.stream()
                    .filter(item -> item.getId().equals(eduId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Educational item not found for ID: " + eduId));

            // Selectively update fields based on request data, preserving existing values for missing fields
            if (educationalBackground.getInstitution() != null) {
                existingBackground.setInstitution(educationalBackground.getInstitution());
            }
            if (educationalBackground.getDegree() != null) {
                existingBackground.setDegree(educationalBackground.getDegree());
            }
            if (educationalBackground.getStartDate() != null) {
                existingBackground.setStartDate(educationalBackground.getStartDate());
            }
            if (educationalBackground.getEndDate() != null) {
                existingBackground.setEndDate(educationalBackground.getEndDate());
            }
            if (educationalBackground.getFocusOfStudies() != null) {
                existingBackground.setFocusOfStudies(educationalBackground.getFocusOfStudies());
            }
            if (educationalBackground.getUserInfo() == null) {
                existingBackground.setUserInfo(existingBackground.getUserInfo());
            } else {
                existingBackground.setUserInfo(educationalBackground.getUserInfo());
            }
            return this.educationalRepository.save(existingBackground);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while updating educational background data: ", e);
        }
    }

    @Override
    public EducationalBackground deleteEducationalBackgroundById(Long userId, Long eduId) {
        try {
            Optional<User> existingUser = this.userRepository.findById(userId);
            if (existingUser.isEmpty()) {
                throw new RuntimeException("User not found");
            }
            Long userInfoId = existingUser.get().getUserInfo().getId();
            List<EducationalBackground> educationalBackground = this.educationalRepository.findAllByUserInfoId(userInfoId);
            if (educationalBackground == null) {
                throw new RuntimeException("Educational items not found for ID: " + userInfoId);
            }
            EducationalBackground educationalItem = educationalBackground.stream()
                    .filter(item -> item.getId().equals(eduId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Educational background not found with ID: " + eduId));
            this.educationalRepository.deleteById(educationalItem.getId());
            return educationalItem;
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while trying to erase educational background data: ", e);
        }
    }

    @Override
    public List<EducationalBackground> findAllByUserId(Long userId) {
        try {
            UserInfo userInfo = this.userInfoRepository.findByUser_Id(userId);
            return this.educationalRepository.findAllByUserInfoId(userInfo.getId());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while trying to fetch all educational background data: ", e);
        }
    }
}
