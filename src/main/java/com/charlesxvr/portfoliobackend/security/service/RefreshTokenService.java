package com.charlesxvr.portfoliobackend.security.service;

import com.charlesxvr.portfoliobackend.security.models.entities.RefreshToken;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public interface RefreshTokenService {
    RefreshToken createRefreshToken(Long id);

    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> verifyExpiration(Optional<RefreshToken> token);
    Long deleteByUserId(Long id);
}
