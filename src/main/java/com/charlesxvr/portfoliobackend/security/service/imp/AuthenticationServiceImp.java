package com.charlesxvr.portfoliobackend.security.service.imp;

import com.charlesxvr.portfoliobackend.security.dto.AuthenticationRequest;
import com.charlesxvr.portfoliobackend.security.dto.AuthenticationResponse;
import com.charlesxvr.portfoliobackend.security.models.User;
import com.charlesxvr.portfoliobackend.security.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthenticationServiceImp implements AuthenticationService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserServiceImp userServiceImp;
    @Autowired
    private JwtServiceImp jwtServiceImp;
    public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        authenticationManager.authenticate(authToken);
        User user = userServiceImp.findByUsername(authenticationRequest.getUsername()).get();
        String jwt = jwtServiceImp.generateToken(user, generateExtraClaims(user));
        return new AuthenticationResponse(jwt);
    }
    private Map<String, Object> generateExtraClaims(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("name", user.getFirstName());
        extraClaims.put("role", user.getRole());
        extraClaims.put("permissions", user.getAuthorities());
        return extraClaims;
    }
}
