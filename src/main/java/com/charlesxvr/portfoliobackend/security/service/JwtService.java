package com.charlesxvr.portfoliobackend.security.service;

import com.charlesxvr.portfoliobackend.security.dto.InvalidateTokenResult;
import com.charlesxvr.portfoliobackend.security.dto.apiResponseDto;
import com.charlesxvr.portfoliobackend.security.models.entities.Token;
import com.charlesxvr.portfoliobackend.security.models.entities.User;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import java.util.Map;
@Service
public interface JwtService {
    Token generateToken(User user, Map<String, Object> generateExtraClaims);
    Token saveToken(Token token);
    Token findTokenByUserId(Long userId);
    Claims extractAllClaims(String jwt);

    InvalidateTokenResult invalidateToken(String jwtToken);
    boolean validateToken(String token);
}
