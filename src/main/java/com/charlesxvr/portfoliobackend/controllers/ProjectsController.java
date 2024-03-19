package com.charlesxvr.portfoliobackend.controllers;

import com.charlesxvr.portfoliobackend.models.entities.Projects;
import com.charlesxvr.portfoliobackend.security.dto.apiResponseDto;
import com.charlesxvr.portfoliobackend.security.models.entities.User;
import com.charlesxvr.portfoliobackend.security.service.UserService;
import com.charlesxvr.portfoliobackend.services.ProjectsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "http://localhost:5173/")
public class ProjectsController {
    private final ProjectsService projectsService;
    private final UserService userService;

    @Autowired
    public ProjectsController(ProjectsService projectsService, UserService userService) {
        this.projectsService = projectsService;
        this.userService = userService;
    }

    // Get all projects for a user
    @GetMapping("/{username}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<List<Projects>> getAllUserProjectsByUser(@PathVariable String username) {
        try {
            Optional<User> existingUser = this.userService.findByUsername(username);
            if (existingUser.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            List<Projects> projectsList = this.projectsService.findAllByUserId(existingUser.get().getId());
            if (projectsList == null) {
                return ResponseEntity.badRequest().body(null);
            }
            return ResponseEntity.ok().body(projectsList);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // Create a new project
    @PostMapping("/{username}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<Projects> createProject(@RequestBody Projects project, @PathVariable String username) {
        try {
            Optional<User> existingUser = this.userService.findByUsername(username);
            Projects createdProject = projectsService.createUserProject(project, existingUser.get().getId());
            return ResponseEntity.status(201).body(createdProject);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // Get a specific project by ID
    @GetMapping("/{username}/item/{projectId}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<?> getProjectById(@PathVariable Long projectId, @PathVariable String username) {
        try {
            Projects project = projectsService.getUserProjectByUserId(projectId, username);
            if (project == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok().body(project);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new apiResponseDto(e.getMessage()));
        }
    }

    // Update an existing project
    @PutMapping("/{username}/item/{projectId}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<?> updateProject(@RequestBody Projects project, @PathVariable Long projectId, @PathVariable String username) {
        try {
            Projects updatedProject = projectsService.updateProject(project, projectId, username);
            return ResponseEntity.ok().body(updatedProject);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Delete a project by ID
    @DeleteMapping("/{username}/item/{projectId}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMINISTRATOR')")
    public ResponseEntity<?> deleteProject(@PathVariable String username, @PathVariable Long projectId) {
        try {
            Optional<User> existingUser = this.userService.findByUsername(username);
            Projects deletedProject = projectsService.deleteProjectById(existingUser.get().getId(), projectId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(deletedProject);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
