package com.charlesxvr.portfoliobackend.controllers;

import com.charlesxvr.portfoliobackend.dto.UserInfoDTO;
import com.charlesxvr.portfoliobackend.models.entities.EducationalBackground;
import com.charlesxvr.portfoliobackend.models.entities.UserInfo;
import com.charlesxvr.portfoliobackend.security.models.entities.User;
import com.charlesxvr.portfoliobackend.security.service.UserService;
import com.charlesxvr.portfoliobackend.services.UserInfoService;
import com.charlesxvr.portfoliobackend.services.imp.UserInfoServiceImp;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("api/users/userinfo")
@CrossOrigin(origins = "http://localhost:5173")
public class UserInfoController {
    private final UserInfoService userInfoService;
    @Autowired
    public UserInfoController(
        UserInfoService userInfoService
    ) {
        this.userInfoService = userInfoService;
    }
    @GetMapping("/{username}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<?> getUserInfo(@PathVariable String username) {
        try {
            System.out.println(username);
            UserInfo userInfo = this.userInfoService.getUserInfo(username);
            System.out.println(userInfo);

            return userInfo != null
                    ? ResponseEntity.ok(userInfo)  // Return 200 with user info if found
                    : ResponseEntity.notFound().build(); // Return 404 for user not found
        } catch (Exception e) {
            // Consider logging the exception for debugging purposes
            return ResponseEntity.internalServerError().body(e.getMessage()); // Return 500 for unexpected errors
        }
    }
    @PostMapping("/{username}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<?> createUserInfo(@RequestBody @Valid UserInfo userInfo, @PathVariable String username) {
        System.out.println(username);
        try {
            System.out.println(userInfo);
            UserInfoDTO userInfoDTO = this.userInfoService.createUserInfo(userInfo, username);
            return ResponseEntity.ok(userInfoDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @PutMapping("/{username}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<?> updateEducationalBackground(@RequestBody UserInfo userInfo, @PathVariable String username) {
        try {
            UserInfo existingUserInfo = this.userInfoService.updateUserInfo(userInfo, username);
            return ResponseEntity.ok().body(existingUserInfo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
