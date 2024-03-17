package com.charlesxvr.portfoliobackend.services;

import com.charlesxvr.portfoliobackend.models.entities.ProfessionalBackground;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProfessionalService {
    // Create a new educational background
    ProfessionalBackground createProfessionalBackground (ProfessionalBackground educationalBackground, Long userId);

    // Retrieve an educational background by its User ID
    ProfessionalBackground getUserProfessionalBackgroundById(Long profId, String username);

    // Update an existing educational background
    ProfessionalBackground updateProfessionalBackground(ProfessionalBackground educationalBackground, Long profId, String username);
    // Delete an educational background by its ID
    ProfessionalBackground deleteOneProfessionalBackgroundById(Long userId, Long profId);

    // Find all educational backgrounds for a user (by user ID)
    List<ProfessionalBackground> findAllByUserId(Long userId);
}
