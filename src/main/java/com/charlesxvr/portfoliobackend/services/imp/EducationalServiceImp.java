package com.charlesxvr.portfoliobackend.services.imp;

import com.charlesxvr.portfoliobackend.models.entities.EducationalBackground;
import com.charlesxvr.portfoliobackend.models.entities.UserInfo;
import com.charlesxvr.portfoliobackend.repositories.EducationalRepository;
import com.charlesxvr.portfoliobackend.repositories.UserInfoRepository;
import com.charlesxvr.portfoliobackend.security.repository.UserRepository;
import com.charlesxvr.portfoliobackend.services.EducationalService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public EducationalBackground getEducationalBackgroundById(Long id) {
        try {
            return this.educationalRepository.findById(id).orElseThrow(RuntimeException::new);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error fetching educational background: ", e);
        }
    }
    public EducationalBackground getEducationalBackgroundByUserId(Long id) {
        try {
            return this.educationalRepository.findByUserInfo_Id(id);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error fetching educational background: ", e);
        }
    }
    @Override
    public EducationalBackground updateEducationalBackground(EducationalBackground educationalBackground) {
        try {
            Long id = educationalBackground.getId();
            EducationalBackground existingBackground = this.educationalRepository.findById(id).orElseThrow(RuntimeException::new);

            existingBackground.setInstitution(educationalBackground.getInstitution());
            existingBackground.setDegree(educationalBackground.getDegree());
            existingBackground.setStartDate(educationalBackground.getStartDate());
            existingBackground.setEndDate(educationalBackground.getEndDate());
            existingBackground.setFocusOfStudies(educationalBackground.getFocusOfStudies());
            existingBackground.setUserInfo(educationalBackground.getUserInfo());

            return this.educationalRepository.save(educationalBackground);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while updating educational background data: ", e);
        }
    }

    @Override
    public EducationalBackground deleteEducationalBackgroundById(Long id) {
        try {
            EducationalBackground educationalBackground = this.educationalRepository.findById(id).orElse(null);
            if (educationalBackground == null) {
                throw new RuntimeException("Educational item not found for ID: " + id);
            }
            this.educationalRepository.deleteById(id);
            return educationalBackground;
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while trying to erase educational background data: ", e);
        }
    }

    @Override
    public List<EducationalBackground> findAllByUserId(Long userId) {
        try {
            return this.educationalRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while trying to fetch all educational background data: ", e);
        }
    }
}
