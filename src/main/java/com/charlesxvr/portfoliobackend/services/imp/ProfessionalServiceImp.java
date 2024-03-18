package com.charlesxvr.portfoliobackend.services.imp;

import com.charlesxvr.portfoliobackend.models.entities.ProfessionalBackground;
import com.charlesxvr.portfoliobackend.models.entities.UserInfo;
import com.charlesxvr.portfoliobackend.repositories.ProfessionalRepository;
import com.charlesxvr.portfoliobackend.repositories.UserInfoRepository;
import com.charlesxvr.portfoliobackend.security.models.entities.User;
import com.charlesxvr.portfoliobackend.security.repository.UserRepository;
import com.charlesxvr.portfoliobackend.services.ProfessionalService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfessionalServiceImp implements ProfessionalService {
    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public ProfessionalBackground createProfessionalBackground(ProfessionalBackground professionalBackground, Long userId) {
        try {
            UserInfo userInfo = userInfoRepository.findByUser_Id(userId);
            if (userInfo == null) {
                throw new RuntimeException("UserInfo not found for user ID: " + userId);
            }
            professionalBackground.setUserInfo(userInfo);
            return professionalRepository.save(professionalBackground);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error creating professional background: " + e.getMessage());
        }
    }

    @Override
    public ProfessionalBackground getUserProfessionalBackgroundById(Long profId, String username) {
        try {
            Optional<User> existingUser = userRepository.findByUsername(username);
            if (existingUser.isEmpty()) {
                throw new RuntimeException("User not found for username: " + username);
            }
            Long userInfoId = existingUser.get().getUserInfo().getId();

            List<ProfessionalBackground> professionalBackgroundList = professionalRepository.findByUserInfo_Id(userInfoId);

            return professionalBackgroundList.stream()
                    .filter(item -> item.getId().equals(profId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Professional background item not found for ID: " + profId));
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error fetching professional background: ", e);
        }
    }

    @Override
    @Transactional
    public ProfessionalBackground updateProfessionalBackground(ProfessionalBackground professionalBackground, Long profId, String username) {
        try {
            Optional<User> existingUser = userRepository.findByUsername(username);
            if (existingUser.isEmpty()) {
                throw new RuntimeException("User not found for username: " + username);
            }
            Long userInfoId = existingUser.get().getUserInfo().getId();

            List<ProfessionalBackground> professionalBackgroundList = professionalRepository.findByUserInfo_Id(userInfoId);
            ProfessionalBackground existingBackground = professionalBackgroundList.stream()
                    .filter(item -> item.getId().equals(profId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Professional background item not found for ID: " + profId));

            // Selectively update fields based on request data, preserving existing values for missing fields
            if (professionalBackground.getCompanyName() != null) {
                existingBackground.setCompanyName(professionalBackground.getCompanyName());
            }
            if (professionalBackground.getJobTitle() != null) {
                existingBackground.setJobTitle(professionalBackground.getJobTitle());
            }
            if (professionalBackground.getStartDate() != null) {
                existingBackground.setStartDate(professionalBackground.getStartDate());
            }
            if (professionalBackground.getEndDate() != null) {
                existingBackground.setEndDate(professionalBackground.getEndDate());
            }
            if (professionalBackground.getSummary() != null) {
                existingBackground.setSummary(professionalBackground.getSummary());
            }
            if (professionalBackground.getUserInfo() == null) {
                existingBackground.setUserInfo(existingBackground.getUserInfo());
            } else {
                existingBackground.setUserInfo(professionalBackground.getUserInfo());
            }
            return professionalRepository.save(existingBackground);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while updating professional background data: ", e);
        }
    }

    @Override
    @Transactional
    public ProfessionalBackground deleteOneProfessionalBackgroundById(Long userId, Long profId) {
        try {
            Optional<User> existingUser = userRepository.findById(userId);
            if (existingUser.isEmpty()) {
                throw new RuntimeException("User not found");
            }
            Long userInfoId = existingUser.get().getUserInfo().getId();
            List<ProfessionalBackground> professionalBackgroundList = professionalRepository.findByUserInfo_Id(userInfoId);
            if (professionalBackgroundList == null) {
                throw new RuntimeException("Professional background items not found for ID: " + userInfoId);
            }
            ProfessionalBackground professionalBackground = professionalBackgroundList.stream()
                    .filter(item -> item.getId().equals(profId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Professional background not found with ID: " + profId));
            professionalRepository.deleteById(professionalBackground.getId());
            return professionalBackground;
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while trying to erase professional background data: ", e);
        }
    }

    @Override
    public List<ProfessionalBackground> findAllByUserId(Long userId) {
        try {
            UserInfo userInfo = this.userInfoRepository.findByUser_Id(userId);
            return professionalRepository.findByUserInfo_Id(userInfo.getId());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while trying to fetch all professional background data: ", e);
        }
    }
}
