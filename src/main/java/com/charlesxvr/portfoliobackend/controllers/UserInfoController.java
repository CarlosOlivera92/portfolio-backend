package com.charlesxvr.portfoliobackend.controllers;

import com.charlesxvr.portfoliobackend.dto.UserInfoDTO;
import com.charlesxvr.portfoliobackend.models.entities.UserInfo;
import com.charlesxvr.portfoliobackend.services.imp.UserInfoServiceImp;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/users/user-info")
@CrossOrigin(origins = "http://localhost:5173")
public class UserInfoController {
    @Autowired
    private UserInfoServiceImp userInfoServiceImp;
    @PostMapping("/{username}")
    public ResponseEntity<?> createUserInfo(@RequestBody @Valid UserInfo userInfo, @PathVariable String username) {
        System.out.println(username);
        try {
            System.out.println(userInfo);
            UserInfoDTO userInfoDTO = this.userInfoServiceImp.createUserInfo(userInfo, username);
            return ResponseEntity.ok(userInfoDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
