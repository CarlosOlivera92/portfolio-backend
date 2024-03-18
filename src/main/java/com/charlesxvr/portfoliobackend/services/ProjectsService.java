package com.charlesxvr.portfoliobackend.services;

import com.charlesxvr.portfoliobackend.models.entities.ProfessionalBackground;
import com.charlesxvr.portfoliobackend.models.entities.Projects;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProjectsService {
    Projects createUserProject (Projects project, Long userId);

    // Retrieve a project by its User ID
    Projects getUserProjectByUserId(Long projId, String username);

    // Update an existing project
    Projects updateProfessionalBackground(Projects project, Long projId, String username);
    // Delete a project by its ID
    Projects deleteProjectById(Long userId, Long projId);

    // Find all educational backgrounds for a user (by user ID)
    List<Projects> findAllByUserId(Long userId);
}
