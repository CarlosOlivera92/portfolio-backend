package com.charlesxvr.portfoliobackend.controllers;

import com.charlesxvr.portfoliobackend.models.entities.Certifications;
import com.charlesxvr.portfoliobackend.services.CertificationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certifications")
@CrossOrigin(origins = "https://solo-resume-3c133.web.app/")
public class CertificationsController {
    private final CertificationsService certificationsService;
    @Autowired
    public  CertificationsController(
            CertificationsService certificationsService
    ) {
        this.certificationsService = certificationsService;
    }
    @GetMapping("/{username}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<List<Certifications>> getAllCertificationsByUser(@PathVariable String username) {
        try {
            List<Certifications> certificationsList = this.certificationsService.getAllByUserInfo(username);
            return ResponseEntity.ok().body(certificationsList);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    @GetMapping("/{username}/courses/{courseId}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<List<Certifications>> getAllCertificationsByCourse(@PathVariable String username, @PathVariable Long courseId) {
        try {
            List<Certifications> certificationsList = this.certificationsService.getAllByCourseId(courseId, username);
            return ResponseEntity.ok().body(certificationsList);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    @GetMapping("/{username}/educational/{educationalId}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<List<Certifications>> getAllCertificationsByEducational(@PathVariable String username, @PathVariable Long educationalId) {
        try {
            List<Certifications> certificationsList = this.certificationsService.getAllByEducationalId(educationalId, username);
            return ResponseEntity.ok().body(certificationsList);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    @PostMapping("/{username}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<Certifications> createNewCertification(@PathVariable String username, @RequestBody Certifications certification) {
        try {
            Certifications newCertification = this.certificationsService.createCertification(certification, username);
            return ResponseEntity.status(HttpStatus.CREATED).body(newCertification);
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    @PostMapping("/{username}/courses/{courseId}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<Certifications> createNewCertificationForExistingCourse(@PathVariable String username, @PathVariable Long courseId, @RequestBody Certifications certification) {
        try {
            Certifications newCertification = this.certificationsService.createCertificationByCourseId(certification, username, courseId);
            return ResponseEntity.status(HttpStatus.CREATED).body(newCertification);
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    @PostMapping("/{username}/educational/{educationalId}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<Certifications> createNewCertification(@PathVariable String username, @PathVariable Long educationalId, @RequestBody Certifications certification) {
        try {
            Certifications newCertification = this.certificationsService.createCertificationByEducationalId(certification, username, educationalId);
            return ResponseEntity.status(HttpStatus.CREATED).body(newCertification);
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    @PutMapping("/{username}/{certificationId}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<Certifications> updateCertificationByUser(@PathVariable String username, @PathVariable Long certificationId, @RequestBody Certifications certification) {
        try {
            Certifications updatedCertification = this.certificationsService.updateCertificationByUserInfo(certification, username, certificationId);
            return ResponseEntity.status(HttpStatus.CREATED).body(updatedCertification);
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    @PutMapping("/{username}/courses/{courseId}/{certificationId}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<Certifications> updateCertificationByCourse(@PathVariable String username, @PathVariable Long courseId, @PathVariable Long certificationId, @RequestBody Certifications certification) {
        try {
            Certifications updatedCertification = this.certificationsService.updateCertificationByCourse(certification, courseId, username, certificationId);
            return ResponseEntity.status(HttpStatus.CREATED).body(updatedCertification);
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    @PutMapping("/{username}/educational/{educationalId}/{certificationId}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<Certifications> updateCertificationByEducational(@PathVariable String username, @PathVariable Long certificationId, @PathVariable Long educationalId, @RequestBody Certifications certification) {
        try {
            Certifications updatedCertification = this.certificationsService.updateCertificationByEducation(certification, educationalId, username, certificationId );
            return ResponseEntity.status(HttpStatus.CREATED).body(updatedCertification);
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    @DeleteMapping("/{username}/{certificationId}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<Long> deleteCertificationByUser(@PathVariable String username, @PathVariable Long certificationId) {
        try {
            Long deletedRows = this.certificationsService.deleteOneByUserInfoAndId(username, certificationId);
            return ResponseEntity.status(HttpStatus.CREATED).body(deletedRows);
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    @DeleteMapping("/{username}/courses/{courseId}/{certificationId}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<Long> deleteCertificationByCourse(@PathVariable String username, @PathVariable Long certificationId, @PathVariable Long courseId) {
        try {
            Long deletedRows = this.certificationsService.deleteOneByCourseAndId(username, courseId, certificationId);
            return ResponseEntity.status(HttpStatus.CREATED).body(deletedRows);
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    @DeleteMapping("/{username}/educational/{educationalId}/{certificationId}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<Long> deleteCertificationByEducational(@PathVariable String username, @PathVariable Long certificationId, @PathVariable Long educationalId) {
        try {
            Long deletedRows = this.certificationsService.deleteOneByEducationalAndId(username,educationalId, certificationId);
            return ResponseEntity.status(HttpStatus.CREATED).body(deletedRows);
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
