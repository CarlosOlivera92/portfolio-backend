package com.charlesxvr.portfoliobackend.security.service;

import com.charlesxvr.portfoliobackend.security.models.User;

import java.util.Map;

public interface JwtService {
    String generateToken(User user, Map<String, Object> generateExtraClaims);

}
