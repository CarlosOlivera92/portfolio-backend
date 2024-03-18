package com.charlesxvr.portfoliobackend.security.service;

import com.charlesxvr.portfoliobackend.security.dto.*;
import com.charlesxvr.portfoliobackend.security.models.entities.User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface AuthenticationService {
    AuthenticationResponse login(AuthenticationRequest authenticationRequest);
    InvalidateTokenResult logout(String jwt);
    UserDto register(User user);
    boolean checkEditPermission(String token, pathUrlDto url);
    Map<String, Object> generateExtraClaims(User user);
}
