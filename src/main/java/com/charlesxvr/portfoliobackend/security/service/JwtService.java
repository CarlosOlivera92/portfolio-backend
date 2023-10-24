package com.charlesxvr.portfoliobackend.security.service;

import com.charlesxvr.portfoliobackend.security.models.Token;
import com.charlesxvr.portfoliobackend.security.models.User;

import java.util.Map;

public interface JwtService {
    Token generateToken(User user, Map<String, Object> generateExtraClaims);

}
