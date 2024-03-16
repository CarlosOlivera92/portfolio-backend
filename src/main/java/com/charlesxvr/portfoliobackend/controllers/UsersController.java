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

import java.util.List;

@RestController
@RequestMapping("api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UsersController {
    private final UserService userService;
    @Autowired
    public UsersController(
            UserService userService
    ) {
        this.userService = userService;
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
    public ResponseEntity<Object> getUserByUsername(@PathVariable String username) {
        try {
            User user = this.userService.findByUsername(username).get();
            UserDto userDto = new UserDto(user);
            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
        }
    }
}
