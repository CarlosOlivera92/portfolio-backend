package com.charlesxvr.portfoliobackend.controllers;

import com.charlesxvr.portfoliobackend.models.entities.ProfessionalBackground;
import com.charlesxvr.portfoliobackend.security.dto.apiResponseDto;
import com.charlesxvr.portfoliobackend.security.models.entities.User;
import com.charlesxvr.portfoliobackend.security.service.UserService;
import com.charlesxvr.portfoliobackend.services.ProfessionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/professional")
@CrossOrigin(origins = "https://solo-resume-3c133.web.app/")
public class ProfessionalBackgroundController {

    private final ProfessionalService professionalService;
    private final UserService userService;

    @Autowired
    public ProfessionalBackgroundController(ProfessionalService professionalService, UserService userService) {
        this.professionalService = professionalService;
        this.userService = userService;
    }


    @GetMapping("/{username}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<List<ProfessionalBackground>> getAllUserProfessionalBackgroundsByUser(@PathVariable String username) {
        try {
            Optional<User> existingUser = this.userService.findByUsername(username);
            List<ProfessionalBackground> professionalBackgroundList = this.professionalService.findAllByUserId(existingUser.get().getId());
            if (professionalBackgroundList == null) {
                return ResponseEntity.badRequest().body(null);
            }
            return ResponseEntity.ok().body(professionalBackgroundList);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @PutMapping("/{username}/item/{professionalId}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<?> updateProfessionalBackground(@RequestBody ProfessionalBackground professionalBackground, @PathVariable Long professionalId, @PathVariable String username) {
        try {
            ProfessionalBackground existingProfessionalBackground = this.professionalService.updateProfessionalBackground(professionalBackground, professionalId, username);
            return ResponseEntity.ok().body(existingProfessionalBackground);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{username}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<ProfessionalBackground> createProfessionalBackground(@RequestBody ProfessionalBackground professionalBackground, @PathVariable String username) {
        try {
            Optional<User> existingUser = this.userService.findByUsername(username);
            if (existingUser.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            ProfessionalBackground response = this.professionalService.createProfessionalBackground(professionalBackground, existingUser.get().getId());
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/{username}/item/{profId}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<?> getProfessionalBackgroundById(@PathVariable Long profId, @PathVariable String username) {
        try {
            ProfessionalBackground professionalBackground = this.professionalService.getUserProfessionalBackgroundById(profId, username);
            if (professionalBackground == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok().body(professionalBackground);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new apiResponseDto(e.getMessage()));
        }
    }

    @DeleteMapping("/{username}/item/{profId}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<?> deleteProfessionalBackground(@PathVariable String username, @PathVariable Long profId) {
        try {
            Optional<User> existingUser = this.userService.findByUsername(username);
            ProfessionalBackground deletedBackground = this.professionalService.deleteOneProfessionalBackgroundById(existingUser.get().getId(), profId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(deletedBackground);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}