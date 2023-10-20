package com.charlesxvr.portfoliobackend.security.controllers;


import com.charlesxvr.portfoliobackend.security.dto.AuthenticationRequest;
import com.charlesxvr.portfoliobackend.security.dto.AuthenticationResponse;
import com.charlesxvr.portfoliobackend.security.models.User;
import com.charlesxvr.portfoliobackend.security.service.imp.AuthenticationServiceImp;
import com.charlesxvr.portfoliobackend.security.service.imp.UserServiceImp;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
public class AuthController {
    @Autowired
    private UserServiceImp userServiceImp;
    @Autowired
    private AuthenticationServiceImp authenticationServiceImp;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PreAuthorize("permitAll")
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login
            (@RequestBody @Valid AuthenticationRequest authRequest) {
        System.out.println("Diezpeso");

        AuthenticationResponse jwtDto = authenticationServiceImp.login(authRequest);
        return ResponseEntity.ok(jwtDto);
    }
    @PreAuthorize("permitAll")
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody @Valid User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return ResponseEntity.status(HttpStatus.CREATED).body(
                this.userServiceImp.newUser(user)
        );
    }
}
