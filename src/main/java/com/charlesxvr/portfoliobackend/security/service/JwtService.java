package com.charlesxvr.portfoliobackend.security.service;

import com.charlesxvr.portfoliobackend.security.models.entities.Token;
import com.charlesxvr.portfoliobackend.security.models.entities.User;

import java.util.Map;

public interface JwtService {
    Token generateToken(User user, Map<String, Object> generateExtraClaims);
    Token saveToken(Token token);
}
