package com.charlesxvr.portfoliobackend.controllers;

import com.charlesxvr.portfoliobackend.security.dto.UserDto;
import com.charlesxvr.portfoliobackend.security.models.entities.User;
import com.charlesxvr.portfoliobackend.security.service.UserService;
import com.charlesxvr.portfoliobackend.security.service.imp.UserServiceImp;
import com.charlesxvr.portfoliobackend.services.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UsersController {
    private final UserService userService;
    private final UserInfoService userInfoService;
    @Autowired
    public UsersController(
            UserService userService,
            UserInfoService userInfoService
    ) {
        this.userService = userService;
        this.userInfoService = userInfoService;
    }
    @PreAuthorize("hasAuthority('READ_ALL_USERS')")
    @GetMapping("/")
    public ResponseEntity<List<UserDto>> getUsers() {
        try {
            List<UserDto> userList = userService.getUsers();
            if (!userList.isEmpty()) {
                return ResponseEntity.ok(userList);
            } else {
                throw new Exception("No users found");
            }
        }  catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @PreAuthorize("permitAll")
    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        try {
            Optional<UserDto> userDto = this.userService.findUserAndInfoByUsername(username);
            return ResponseEntity.ok().body(userDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
        }
    }
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMINISTRATOR')")
    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteByUsername(@PathVariable String username) {
        try {
            this.userService.deleteUser(username);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/profile-pic/{username}/file")
    public ResponseEntity<?> updateProfilePic(@RequestBody MultipartFile file, @PathVariable String username) {
        try {
            userInfoService.updateProfilePic(file, username);
            return ResponseEntity.ok().body("Profile pic updated successfully!");
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to update profile pic: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/profile-pic/{username}")
    public ResponseEntity<?> getProfilePic(@PathVariable String username) {
        try {
            String profilePic = this.userInfoService.getProfilePic(username);
            return ResponseEntity.ok().body(profilePic);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
