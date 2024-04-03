package com.charlesxvr.portfoliobackend.controllers;

import com.charlesxvr.portfoliobackend.models.entities.Courses;
import com.charlesxvr.portfoliobackend.security.dto.apiResponseDto;
import com.charlesxvr.portfoliobackend.security.models.entities.User;
import com.charlesxvr.portfoliobackend.security.service.UserService;
import com.charlesxvr.portfoliobackend.services.CoursesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "https://solo-resume-3c133.web.app/")
public class CoursesController {
    private final CoursesService courseService;
    private final UserService userService;

    @Autowired
    public CoursesController(CoursesService courseService, UserService userService) {
        this.courseService = courseService;
        this.userService = userService;
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMINISTRATOR')")

    public ResponseEntity<List<Courses>> getAllUserCoursesByUser(@PathVariable String username) {
        try {
            Optional<User> existingUser = this.userService.findByUsername(username);
            List<Courses> coursesList = this.courseService.findAllByUserId(existingUser.get().getId());
            if (coursesList == null) {
                return ResponseEntity.badRequest().body(null);
            }
            return ResponseEntity.ok().body(coursesList);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @PutMapping("/{username}/item/{courseId}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<?> updateCourse(@RequestBody Courses course, @PathVariable Long courseId, @PathVariable String username) {
        try {
            Courses existingCourse = this.courseService.updateCourse(course, courseId, username);
            return ResponseEntity.ok().body(existingCourse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{username}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<Courses> createCourse(@RequestBody Courses course, @PathVariable String username) {
        try {
            Optional<User> existingUser = this.userService.findByUsername(username);
            if (existingUser.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Courses response = this.courseService.createCourse(course, existingUser.get().getId());
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/{username}/item/{courseId}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<?> getCourseById(@PathVariable Long courseId, @PathVariable String username) {
        try {
            Courses course = this.courseService.getUserCourseById(courseId, username);
            if (course == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok().body(course);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new apiResponseDto(e.getMessage()));
        }
    }
    @DeleteMapping("/{username}/item/{courseId}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<?> deleteCourse(@PathVariable String username, @PathVariable Long courseId) {
        try {
            Optional<User> existingUser = this.userService.findByUsername(username);
            Courses deletedCourse = this.courseService.deleteCourse(existingUser.get().getId(), courseId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(deletedCourse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
