package com.charlesxvr.portfoliobackend.services.imp;

import com.charlesxvr.portfoliobackend.models.entities.Courses;
import com.charlesxvr.portfoliobackend.models.entities.UserInfo;
import com.charlesxvr.portfoliobackend.repositories.CoursesRepository;
import com.charlesxvr.portfoliobackend.repositories.UserInfoRepository;
import com.charlesxvr.portfoliobackend.security.models.entities.User;
import com.charlesxvr.portfoliobackend.security.repository.UserRepository;
import com.charlesxvr.portfoliobackend.services.CoursesService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CoursesServiceImp implements CoursesService {
    private final CoursesRepository coursesRepository;
    private final UserInfoRepository userInfoRepository;
    private final UserRepository userRepository;
    @Autowired
    public CoursesServiceImp(
            CoursesRepository coursesRepository,
            UserInfoRepository userInfoRepository,
            UserRepository userRepository
    ) {
        this.coursesRepository = coursesRepository;
        this.userInfoRepository = userInfoRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Courses createCourse(Courses course, Long userId) {
        try {
            UserInfo userInfo = this.userInfoRepository.findByUser_Id(userId);
            if (userInfo == null) {
                throw new RuntimeException("UserInfo not found for user ID: " + userId);
            }
            course.setUserInfo(userInfo);
            return this.coursesRepository.save(course);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error creating course: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Courses getUserCourseById(Long courseId, String username) {
        try {
            Optional<User> user = this.userRepository.findByUsername(username);
            if (user.isEmpty()) {
                throw new RuntimeException("User not found for username: " + username);
            }
            Long userInfoId = user.get().getUserInfo().getId();
            List<Courses> coursesList = this.coursesRepository.findByUserInfo_Id(userInfoId);
            return coursesList.stream()
                    .filter(item -> item.getId().equals(courseId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Course item not found for ID: " + courseId));

        } catch (Exception e) {
            throw new RuntimeException("Unexpected error fetching the course: " + e.getMessage());
        }
    }

    @Override
    public Courses updateCourse(Courses course, Long courseId, String username) {
        try {
            Optional<User> existingUser = userRepository.findByUsername(username);
            if (existingUser.isEmpty()) {
                throw new RuntimeException("User not found for username: " + username);
            }
            Long userInfoId = existingUser.get().getUserInfo().getId();

            List<Courses> coursesList = this.coursesRepository.findByUserInfo_Id(userInfoId);
            Courses existingCourse = coursesList.stream()
                    .filter(item -> item.getId().equals(courseId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Course item not found for ID: " + courseId));

            // Selectively update fields based on request data, preserving existing values for missing fields
            if (course.getCourseName() != null) {
                existingCourse.setCourseName(course.getCourseName());
            }
            if (course.getInstitution() != null) {
                existingCourse.setInstitution(course.getInstitution());
            }
            if (course.getStartDate() != null) {
                existingCourse.setStartDate(course.getStartDate());
            }
            if (course.getEndDate() != null) {
                existingCourse.setEndDate(course.getEndDate());
            }
            if (course.getFocusOfStudies() != null) {
                existingCourse.setFocusOfStudies(course.getFocusOfStudies());
            }
            if (course.getUserInfo() == null) {
                existingCourse.setUserInfo(existingCourse.getUserInfo());
            } else {
                existingCourse.setUserInfo(course.getUserInfo());
            }
            return this.coursesRepository.save(existingCourse);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while updating courses data: " + e.getMessage());
        }
    }

    @Override
    public Courses deleteCourse(Long userId, Long courseId) {
        try {
            Optional<User> existingUser = userRepository.findById(userId);
            if (existingUser.isEmpty()) {
                throw new RuntimeException("User not found");
            }
            Long userInfoId = existingUser.get().getUserInfo().getId();
            List<Courses> coursesList = coursesRepository.findByUserInfo_Id(userInfoId);
            if (coursesList == null) {
                throw new RuntimeException("Courses items not found for ID: " + userInfoId);
            }
            Courses course = coursesList.stream()
                    .filter(item -> item.getId().equals(courseId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));
            this.coursesRepository.deleteById(course.getId());
            return course;
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while trying to erase a course: " + e.getMessage());
        }
    }

    @Override
    public List<Courses> findAllByUserId(Long userId) {
        try {
            return this.coursesRepository.findByUserInfo_Id(userId);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while trying to fetch all courses: " + e.getMessage());
        }
    }
}
