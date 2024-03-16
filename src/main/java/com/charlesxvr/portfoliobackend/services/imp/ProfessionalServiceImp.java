package com.charlesxvr.portfoliobackend.services.imp;

import com.charlesxvr.portfoliobackend.models.entities.ProfessionalBackground;
import com.charlesxvr.portfoliobackend.services.ProfessionalService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfessionalServiceImp implements ProfessionalService {
    @Override
    public ProfessionalBackground createProfessionalBackground(ProfessionalBackground educationalBackground, Long userId) {
        return null;
    }

    @Override
    public ProfessionalBackground getUserProfessionalBackgroundById(Long profId, String username) {
        return null;
    }

    @Override
    public ProfessionalBackground updateProfessionalBackground(ProfessionalBackground educationalBackground, Long profId, String username) {
        return null;
    }

    @Override
    public ProfessionalBackground deleteOneProfessionalBackgroundById(Long userId, Long profId) {
        return null;
    }

    @Override
    public List<ProfessionalBackground> findAllByUserId(Long userId) {
        return null;
    }
}
