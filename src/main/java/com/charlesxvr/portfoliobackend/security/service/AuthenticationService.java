package com.charlesxvr.portfoliobackend.security.service;

import com.charlesxvr.portfoliobackend.security.dto.AuthenticationRequest;
import com.charlesxvr.portfoliobackend.security.dto.AuthenticationResponse;
import com.charlesxvr.portfoliobackend.security.dto.UserDto;
import com.charlesxvr.portfoliobackend.security.dto.pathUrlDto;
import com.charlesxvr.portfoliobackend.security.models.entities.User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface AuthenticationService {
    AuthenticationResponse login(AuthenticationRequest authenticationRequest);
    UserDto register(User user);
    boolean checkEditPermission(String token, pathUrlDto url);
    Map<String, Object> generateExtraClaims(User user);
}
