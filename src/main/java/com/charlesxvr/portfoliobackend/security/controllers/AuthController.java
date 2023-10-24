package com.charlesxvr.portfoliobackend.security.controllers;


import com.charlesxvr.portfoliobackend.security.dto.AuthenticationRequest;
import com.charlesxvr.portfoliobackend.security.dto.AuthenticationResponse;
import com.charlesxvr.portfoliobackend.security.dto.UserDto;
import com.charlesxvr.portfoliobackend.security.models.JwtResponse;
import com.charlesxvr.portfoliobackend.security.models.entities.Token;
import com.charlesxvr.portfoliobackend.security.models.entities.User;
import com.charlesxvr.portfoliobackend.security.service.imp.AuthenticationServiceImp;
import com.charlesxvr.portfoliobackend.security.service.imp.JwtServiceImp;
import com.charlesxvr.portfoliobackend.security.service.imp.UserServiceImp;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/auth")
public class AuthController {
    @Autowired
    private UserServiceImp userServiceImp;
    @Autowired
    private JwtServiceImp jwtServiceImp;
    @Autowired
    private AuthenticationServiceImp authenticationServiceImp;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PreAuthorize("permitAll")
    @PostMapping("/signin")
    public ResponseEntity<?> login
            (@RequestBody @Valid AuthenticationRequest authRequest) {
        AuthenticationResponse jwtDto = authenticationServiceImp.login(authRequest);
        return ResponseEntity.ok(new JwtResponse(jwtDto.getJwt(),jwtDto.getRefreshToken(),jwtDto.getUser().getId(), jwtDto.getUser().getUsername(), jwtDto.getUser().getEmail(), (List<?>) jwtDto.getUser().getAuthorities()));
    }
    @PreAuthorize("permitAll")
    @PostMapping("/signup")
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Token token = this.jwtServiceImp.generateToken(user, authenticationServiceImp.generateExtraClaims(user));
        this.userServiceImp.newUser(user, token);
        UserDto userResponse = new UserDto(user);

        return ResponseEntity.ok(userResponse);
    }
}
