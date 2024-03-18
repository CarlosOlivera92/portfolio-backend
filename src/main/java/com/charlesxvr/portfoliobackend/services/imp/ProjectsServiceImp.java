package com.charlesxvr.portfoliobackend.services.imp;

import com.charlesxvr.portfoliobackend.models.entities.Projects;
import com.charlesxvr.portfoliobackend.models.entities.UserInfo;
import com.charlesxvr.portfoliobackend.repositories.ProjectsRepository;
import com.charlesxvr.portfoliobackend.repositories.UserInfoRepository;
import com.charlesxvr.portfoliobackend.security.models.entities.User;
import com.charlesxvr.portfoliobackend.security.repository.UserRepository;
import com.charlesxvr.portfoliobackend.services.ProjectsService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class ProjectsServiceImp implements ProjectsService {
    private final ProjectsRepository projectsRepository;
    private final UserRepository userRepository;
    private final UserInfoRepository userInfoRepository;
    @Autowired
    public ProjectsServiceImp(ProjectsRepository projectsRepository, UserRepository userRepository, UserInfoRepository userInfoRepository) {
        this.projectsRepository = projectsRepository;
        this.userRepository = userRepository;
        this.userInfoRepository = userInfoRepository;
    }

    @Override
    @Transactional
    public Projects createUserProject(Projects project, Long userId) {
        try {
            Optional<User> existingUser = userRepository.findById(userId);
            if (existingUser.isEmpty()) {
                throw new RuntimeException("User not found");
            }
            project.setUserInfo(existingUser.get().getUserInfo()); // Associate project with user
            return projectsRepository.save(project);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error creating project: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Projects getUserProjectByUserId(Long projId, String username) {
        try {
            Optional<User> user = userRepository.findByUsername(username);
            if (user.isEmpty()) {
                throw new RuntimeException("User not found for username: " + username);
            }
            UserInfo userInfo = user.get().getUserInfo();
            List<Projects> projectsList = this.projectsRepository.findByUserInfo_Id(userInfo.getId());
            return projectsList.stream()
                    .filter(item -> item.getId().equals(projId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Project item not found for ID: " + projId));
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error fetching the project: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Projects updateProject(Projects project, Long projId, String username) {
        try {
            Optional<User> existingUser = userRepository.findByUsername(username);
            if (existingUser.isEmpty()) {
                throw new RuntimeException("User not found");
            }
            UserInfo userInfo = existingUser.get().getUserInfo();
            List<Projects> existingProjects = projectsRepository.findByUserInfo_Id(userInfo.getId());
            Projects existingProject = existingProjects.stream()
                    .filter(item -> item.getId().equals(projId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Project item not found for ID: " + projId));
            // Selectively update fields based on request data, preserving existing values for missing fields
            if (project.getProjectName() != null) {
                existingProject.setProjectName(project.getProjectName());
            }
            if (project.getProjectUrl() != null) {
                existingProject.setProjectUrl(project.getProjectUrl());
            }
            if (project.getProjectRepoUrl() != null) {
                existingProject.setProjectRepoUrl(project.getProjectRepoUrl());
            }
            if (project.getSummary() != null) {
                existingProject.setSummary(project.getSummary());
            }
            if (project.getUserInfo() == null) {
                existingProject.setUserInfo(existingProject.getUserInfo());
            } else {
                existingProject.setUserInfo(project.getUserInfo());
            }

            return projectsRepository.save(existingProject);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while updating project data: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Projects deleteProjectById(Long userId, Long projId) {
        try {
            Optional<User> existingUser = userRepository.findById(userId);
            if (existingUser.isEmpty()) {
                throw new RuntimeException("User not found");
            }
            UserInfo userInfo = existingUser.get().getUserInfo();
            List<Projects> existingProjects = projectsRepository.findByUserInfo_Id(userInfo.getId());
            Projects project = existingProjects.stream()
                    .filter(item -> item.getId().equals(projId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Project item not found for ID: " + projId));

            projectsRepository.deleteById(project.getId());
            return project;
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while trying to erase a project: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public List<Projects> findAllByUserId(Long userId) {
        try {
            UserInfo userInfo = this.userInfoRepository.findByUser_Id(userId);
            return this.projectsRepository.findByUserInfo_Id(userInfo.getId());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while trying to fetch all courses: " + e.getMessage());
        }
    }
}
