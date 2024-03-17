package com.charlesxvr.portfoliobackend.services;

import com.charlesxvr.portfoliobackend.models.entities.Courses;
import com.charlesxvr.portfoliobackend.models.entities.ProfessionalBackground;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CoursesService {
    Courses createCourse (Courses course, Long userId);

    // Retrieve a course by its User ID
    Courses getUserCourseById(Long courseId, String username);

    // Update an existing course
    Courses updateCourse(Courses course, Long courseId, String username);
    // Delete a course  by its ID
    Courses deleteCourse(Long userId, Long courseId);

    // Find all courses backgrounds for a user (by user ID)
    List<Courses> findAllByUserId(Long userId);
}
