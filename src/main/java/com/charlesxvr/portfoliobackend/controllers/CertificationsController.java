package com.charlesxvr.portfoliobackend.controllers;

import com.charlesxvr.portfoliobackend.models.entities.Certifications;
import com.charlesxvr.portfoliobackend.services.CertificationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certifications")
@CrossOrigin(origins = "http://localhost:5173/")
public class CertificationsController {
    private final CertificationsService certificationsService;
    @Autowired
    public  CertificationsController(
            CertificationsService certificationsService
    ) {
        this.certificationsService = certificationsService;
    }
    @GetMapping("/{username}")
    public ResponseEntity<List<Certifications>> getAllCertificationsByUser(@PathVariable String username) {
        try {
            List<Certifications> certificationsList = this.certificationsService.getAllByUserInfo(username);
            return ResponseEntity.ok().body(certificationsList);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    @GetMapping("/{username}/courses/{courseId}")
    public ResponseEntity<List<Certifications>> getAllCertificationsByCourse(@PathVariable String username, @PathVariable Long courseId) {
        try {
            List<Certifications> certificationsList = this.certificationsService.getAllByCourseId(courseId, username);
            return ResponseEntity.ok().body(certificationsList);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    @GetMapping("/{username}/educational/{educationalId}")
    public ResponseEntity<List<Certifications>> getAllCertificationsByEducational(@PathVariable String username, @PathVariable Long educationalId) {
        try {
            List<Certifications> certificationsList = this.certificationsService.getAllByEducationalId(educationalId, username);
            return ResponseEntity.ok().body(certificationsList);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    @PostMapping("/{username}")
    public ResponseEntity<Certifications> createNewCertification(@PathVariable String username, @RequestBody Certifications certification) {
        try {
            Certifications newCertification = this.certificationsService.createCertification(certification, username);
            return ResponseEntity.status(HttpStatus.CREATED).body(newCertification);
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    @PostMapping("/{username}/courses/{courseId}")
    public ResponseEntity<Certifications> createNewCertificationForExistingCourse(@PathVariable String username, @PathVariable Long courseId, @RequestBody Certifications certification) {
        try {
            Certifications newCertification = this.certificationsService.createCertificationByCourseId(certification, username, courseId);
            return ResponseEntity.status(HttpStatus.CREATED).body(newCertification);
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    @PostMapping("/{username}/educational/{educationalId}")
    public ResponseEntity<Certifications> createNewCertification(@PathVariable String username, @PathVariable Long educationalId, @RequestBody Certifications certification) {
        try {
            Certifications newCertification = this.certificationsService.createCertificationByEducationalId(certification, username, educationalId);
            return ResponseEntity.status(HttpStatus.CREATED).body(newCertification);
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
