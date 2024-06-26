package com.charlesxvr.portfoliobackend.services;

import com.charlesxvr.portfoliobackend.models.entities.EducationalBackground;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EducationalService {
    // Create a new educational background
    EducationalBackground createEducationalBackground(EducationalBackground educationalBackground, Long userId);

    // Retrieve an educational background by its ID
    EducationalBackground getUserEducationalBackgroundById(Long id, String username);

    // Update an existing educational background
    EducationalBackground updateEducationalBackground(EducationalBackground educationalBackground, Long eduId, String username);
    // Delete an educational background by its ID
    EducationalBackground deleteEducationalBackgroundById(Long userId, Long eduId);

    // Find all educational backgrounds for a user (by user ID)
    List<EducationalBackground> findAllByUserId(Long userId);
}
