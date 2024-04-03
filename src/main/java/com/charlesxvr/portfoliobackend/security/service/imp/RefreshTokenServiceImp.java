package com.charlesxvr.portfoliobackend.security.service.imp;

import com.charlesxvr.portfoliobackend.exceptions.TokenRefreshException;
import com.charlesxvr.portfoliobackend.security.models.entities.RefreshToken;
import com.charlesxvr.portfoliobackend.security.models.entities.User;
import com.charlesxvr.portfoliobackend.security.repository.RefreshTokenRepository;
import com.charlesxvr.portfoliobackend.security.repository.UserRepository;
import com.charlesxvr.portfoliobackend.security.service.RefreshTokenService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImp implements RefreshTokenService {
    @Value("${security.jwt.jwtRefreshExpirationMs}")
    private Long REFRESH_TOKEN_DURATION;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public RefreshToken createRefreshToken(Long id) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(userRepository.findById(id).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(REFRESH_TOKEN_DURATION));
        refreshToken.setToken(UUID.randomUUID().toString());

        return this.refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public Optional<RefreshToken> verifyExpiration(Optional<RefreshToken> token) {
        if (token.isEmpty()) {
            throw new RuntimeException("Unable to find refresh token");
        }
        if (token.get().getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.deleteByToken(token.get().getToken());
            throw new TokenRefreshException(token.get().getToken(), "Invalid Token");
        }
        return token;
    }

    @Override
    public Long deleteByUserId(Long id) {
        Optional<User> user = userRepository.findById(id);
        return (long) refreshTokenRepository.deleteByUserId(user.get().getId());
    }
}
