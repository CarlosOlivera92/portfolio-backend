package com.charlesxvr.portfoliobackend.controllers;

import com.charlesxvr.portfoliobackend.models.entities.EducationalBackground;
import com.charlesxvr.portfoliobackend.security.models.entities.User;
import com.charlesxvr.portfoliobackend.security.service.UserService;
import com.charlesxvr.portfoliobackend.services.EducationalService;
import com.charlesxvr.portfoliobackend.services.imp.EducationalServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/educational")
@CrossOrigin(origins = "http://localhost:5173/")
public class EducationalController {

    private final EducationalService educationalService;
    private final UserService userService;
    @Autowired
    public EducationalController(EducationalService educationalService, UserService userService){
        this.educationalService=educationalService;
        this.userService = userService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<List<EducationalBackground>> getAllUserEducationalBackgrounds(@PathVariable String username) {
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
    @PutMapping("/")
    public ResponseEntity<EducationalBackground> updateEducationalBackground(@RequestBody EducationalBackground educationalBackground) {
        try {
            EducationalBackground existingEducationalBackground = this.educationalService.updateEducationalBackground(educationalBackground);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @PostMapping("/{username}")
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
}
