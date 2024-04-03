package com.charlesxvr.portfoliobackend.controllers;

import com.charlesxvr.portfoliobackend.models.entities.EducationalBackground;
import com.charlesxvr.portfoliobackend.security.dto.apiResponseDto;
import com.charlesxvr.portfoliobackend.security.models.entities.User;
import com.charlesxvr.portfoliobackend.security.service.UserService;
import com.charlesxvr.portfoliobackend.services.EducationalService;
import com.charlesxvr.portfoliobackend.services.imp.EducationalServiceImp;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/educational")
@CrossOrigin(origins = "https://solo-resume-3c133.web.app/")
public class EducationalController {

    private final EducationalService educationalService;
    private final UserService userService;
    @Autowired
    public EducationalController(EducationalService educationalService, UserService userService){
        this.educationalService=educationalService;
        this.userService = userService;
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<List<EducationalBackground>> getAllUserEducationalBackgroundsByUser(@PathVariable String username) {
        try {
            Optional<User> existingUser = this.userService.findByUsername(username);
            List<EducationalBackground> educationalBackgroundList = this.educationalService.findAllByUserId(existingUser.get().getId());
            if(educationalBackgroundList == null){
                return ResponseEntity.badRequest().body(null);
            }
            return ResponseEntity.ok().body(educationalBackgroundList);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    @PutMapping("/{username}/item/{educationalId}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<?> updateEducationalBackground(@RequestBody EducationalBackground educationalBackground, @PathVariable Long educationalId, @PathVariable String username) {
        try {
            EducationalBackground existingEducationalBackground = this.educationalService.updateEducationalBackground(educationalBackground, educationalId, username);
            return ResponseEntity.ok().body(existingEducationalBackground);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/{username}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<EducationalBackground> createEducationalBackground(@RequestBody EducationalBackground educationalBackground, @PathVariable String username) {
        try {
            Optional<User> existingUser = this.userService.findByUsername(username);
            if (existingUser.isEmpty()) {
                return ResponseEntity.status(404).build();
            }
            EducationalBackground response = this.educationalService.createEducationalBackground(educationalBackground, existingUser.get().getId());
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    @GetMapping("/{username}/item/{eduId}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<?> getEducationBackgroundById(@PathVariable Long eduId, @PathVariable String username) {
        try {
            EducationalBackground educationalBackground = this.educationalService.getUserEducationalBackgroundById(eduId, username);
            if(educationalBackground == null){
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok().body(educationalBackground);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new apiResponseDto(e.getMessage()));
        }
    }
    @DeleteMapping("/{username}/item/{eduId}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<?> deleteEducationalItem(@PathVariable String username, @PathVariable Long eduId) {
        try {
            Optional<User> existingUser = this.userService.findByUsername(username);
            EducationalBackground deletedBackground = this.educationalService.deleteEducationalBackgroundById(existingUser.get().getId(), eduId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(deletedBackground);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
