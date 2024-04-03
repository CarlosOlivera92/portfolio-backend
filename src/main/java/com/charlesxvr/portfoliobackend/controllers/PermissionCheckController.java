package com.charlesxvr.portfoliobackend.controllers;

import com.charlesxvr.portfoliobackend.security.dto.apiResponseDto;
import com.charlesxvr.portfoliobackend.security.dto.pathUrlDto;

import com.charlesxvr.portfoliobackend.security.service.imp.AuthenticationServiceImp;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/check-permission")
@CrossOrigin(origins = "https://solo-resume-3c133.web.app/")

public class PermissionCheckController {


    @Autowired
    private AuthenticationServiceImp authenticationServiceImp;

    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMINISTRATOR')")
    @PostMapping("/edit-profile")
    public ResponseEntity<apiResponseDto> checkEditProfilePermission(@Valid @RequestHeader("Authorization") String token, @RequestBody pathUrlDto url) {
        try {
            String jwt = token.replace("Bearer ", "");

            boolean hasAccess = authenticationServiceImp.checkEditPermission(jwt, url);
            if (hasAccess) {
                return ResponseEntity.ok(new apiResponseDto("Granted Access"));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new apiResponseDto("Access Denied"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new apiResponseDto(e.getMessage()));
        }
    }
}
