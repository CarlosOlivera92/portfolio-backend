package com.charlesxvr.portfoliobackend.services.imp;

import com.charlesxvr.portfoliobackend.models.entities.Certifications;
import com.charlesxvr.portfoliobackend.models.entities.Courses;
import com.charlesxvr.portfoliobackend.models.entities.EducationalBackground;
import com.charlesxvr.portfoliobackend.models.entities.UserInfo;
import com.charlesxvr.portfoliobackend.repositories.CertificationsRepository;
import com.charlesxvr.portfoliobackend.repositories.CoursesRepository;
import com.charlesxvr.portfoliobackend.repositories.EducationalRepository;
import com.charlesxvr.portfoliobackend.security.models.entities.User;
import com.charlesxvr.portfoliobackend.security.repository.UserRepository;
import com.charlesxvr.portfoliobackend.services.CertificationsService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CertificationsServiceImp implements CertificationsService {
    private final UserRepository userRepository;
    private final CertificationsRepository certificationsRepository;
    private final EducationalRepository educationalRepository;

    private final CoursesRepository coursesRepository;
    public CertificationsServiceImp(
            UserRepository userRepository,
            CertificationsRepository certificationsRepository,
            EducationalRepository educationalRepository,
            CoursesRepository coursesRepository
    ) {
        this.userRepository = userRepository;
        this.certificationsRepository = certificationsRepository;
        this.educationalRepository = educationalRepository;
        this.coursesRepository = coursesRepository;
    }
    @Override
    @Transactional
    public Certifications createCertification(Certifications certification, String username) {
        try {
            Optional<User> existingUser = this.userRepository.findByUsername(username);
            if (existingUser.isEmpty()) {
                throw new RuntimeException("User not found for username: " + username);
            }
            UserInfo userInfo = existingUser.get().getUserInfo();
            certification.setUserInfo(userInfo);
            return this.certificationsRepository.save(certification);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public Certifications createCertificationByEducationalId(Certifications certification, String username, Long eduId) {
        try {
            Optional<User> existingUser = this.userRepository.findByUsername(username);
            if (existingUser.isEmpty()) {
                throw new RuntimeException("User not found for username: " + username);
            }
            UserInfo userInfo = existingUser.get().getUserInfo();
            certification.setUserInfo(userInfo);
            EducationalBackground educationalBackground = this.educationalRepository.findByUserInfo_IdAndId(userInfo.getId(), eduId);
            certification.setEducationalBackground(educationalBackground);
            return this.certificationsRepository.save(certification);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public Certifications createCertificationByCourseId(Certifications certification, String username, Long courseId) {
        try {
            Optional<User> existingUser = this.userRepository.findByUsername(username);
            if (existingUser.isEmpty()) {
                throw new RuntimeException("User not found for username: " + username);
            }
            UserInfo userInfo = existingUser.get().getUserInfo();
            certification.setUserInfo(userInfo);
            Courses course = this.coursesRepository.findByUserInfo_IdAndId(userInfo.getId(), courseId);
            certification.setCourses(course);
            return this.certificationsRepository.save(certification);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public Certifications updateCertificationByEducation(Certifications certification, Long eduId, String username, Long certificationId) {
        try {
            Optional<User> existingUser = this.userRepository.findByUsername(username);
            if (existingUser.isEmpty()) {
                throw new RuntimeException("User not found for username: " + username);
            }
            UserInfo userInfo = existingUser.get().getUserInfo();
            Certifications existingCertification = this.certificationsRepository.findByEducationalBackground_IdAndId(eduId, certificationId);
            if (existingCertification.equals(null)){
                throw new RuntimeException("Certification item not found for ID: " + certificationId);
            }
            // Selectively update fields based on request data, preserving existing values for missing fields
            if (certification.getDegree() != null) {
                existingCertification.setDegree(certification.getDegree());
            }
            if (certification.getCertificationUrl() != null) {
                existingCertification.setCertificationUrl(certification.getCertificationUrl());
            }
            if (certification.getEducationalBackground() == null) {
                existingCertification.setEducationalBackground(existingCertification.getEducationalBackground());
            }
            if (certification.getUserInfo() == null || certification.getUserInfo() != existingCertification.getUserInfo()) {
                existingCertification.setUserInfo(existingCertification.getUserInfo());
            } else {
                existingCertification.setUserInfo(userInfo);
            }
            return this.certificationsRepository.save(existingCertification);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while updating certifications data: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Certifications updateCertificationByCourse(Certifications certification, Long courseId, String username, Long certificationId) {
        try {
            Optional<User> existingUser = this.userRepository.findByUsername(username);
            if (existingUser.isEmpty()) {
                throw new RuntimeException("User not found for username: " + username);
            }
            UserInfo userInfo = existingUser.get().getUserInfo();
            Certifications existingCertification = this.certificationsRepository.findByCourses_IdAndId(courseId, certificationId);
            if (existingCertification.equals(null)){
                throw new RuntimeException("Certification item not found for ID: " + certificationId);
            }
            // Selectively update fields based on request data, preserving existing values for missing fields
            if (certification.getDegree() != null) {
                existingCertification.setDegree(certification.getDegree());
            }
            if (certification.getCertificationUrl() != null) {
                existingCertification.setCertificationUrl(certification.getCertificationUrl());
            }
            if (certification.getCourses() == null) {
                existingCertification.setCourses(existingCertification.getCourses());
            }
            if (certification.getUserInfo() == null || certification.getUserInfo() != existingCertification.getUserInfo()) {
                existingCertification.setUserInfo(existingCertification.getUserInfo());
            } else {
                existingCertification.setUserInfo(userInfo);
            }
            return this.certificationsRepository.save(existingCertification);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while updating certifications data: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Certifications updateCertificationByUserInfo(Certifications certification, String username, Long certificationId) {
        try {
            Optional<User> existingUser = this.userRepository.findByUsername(username);
            if (existingUser.isEmpty()) {
                throw new RuntimeException("User not found for username: " + username);
            }
            Long userInfoId = existingUser.get().getUserInfo().getId();
            Certifications existingCertification = this.certificationsRepository.findByUserInfo_IdAndId(userInfoId, certificationId);
            if (existingCertification.equals(null)){
                throw new RuntimeException("Certification item not found for ID: " + certificationId);
            }
            // Selectively update fields based on request data, preserving existing values for missing fields
            if (certification.getDegree() != null) {
                existingCertification.setDegree(certification.getDegree());
            }
            if (certification.getCertificationUrl() != null) {
                existingCertification.setCertificationUrl(certification.getCertificationUrl());
            }
            if (certification.getUserInfo() == null || certification.getUserInfo() != existingCertification.getUserInfo()) {
                existingCertification.setUserInfo(existingCertification.getUserInfo());
            } else {
                existingCertification.setUserInfo(certification.getUserInfo());
            }
            return this.certificationsRepository.save(existingCertification);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while updating certifications data: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public List<Certifications> getAllByUserInfo(String username) {
        try {
            Optional<User> existingUser = this.userRepository.findByUsername(username);
            if (existingUser.isEmpty()) {
                throw new RuntimeException("User not found for username: " + username);
            }
            UserInfo userInfo = existingUser.get().getUserInfo();
            return this.certificationsRepository.findAllByUserInfo_Id(userInfo.getId());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while fetching certifications data: " + e.getMessage());

        }
    }

    @Override
    @Transactional
    public List<Certifications> getAllByEducationalId(Long educationalId, String username) {
        try {
            Optional<User> existingUser = this.userRepository.findByUsername(username);
            if (existingUser.isEmpty()) {
                throw new RuntimeException("User not found for username: " + username);
            }
            List<EducationalBackground> educationalBackgroundList = this.educationalRepository.findAllByUserInfoId(existingUser.get().getUserInfo().getId());
            EducationalBackground existingEducationalBackground = educationalBackgroundList.stream()
                    .filter(item -> item.getId().equals(educationalId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Educational item not found for ID: " + educationalId));
            return this.certificationsRepository.findAllByEducationalBackground_Id(existingEducationalBackground.getId());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while fetching certifications data: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public List<Certifications> getAllByCourseId(Long courseId, String username) {
        try {
            Optional<User> existingUser = this.userRepository.findByUsername(username);
            if (existingUser.isEmpty()) {
                throw new RuntimeException("User not found for username: " + username);
            }
            List<Courses> coursesList = this.coursesRepository.findByUserInfo_Id(existingUser.get().getUserInfo().getId());
            Courses existingCourse = coursesList.stream()
                    .filter(item -> item.getId().equals(courseId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Course item not found for ID: " + courseId));
            return this.certificationsRepository.findAllByEducationalBackground_Id(existingCourse.getId());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while fetching certifications data: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Long deleteOneByEducationalAndId(String username, Long eduId, Long certificationId) {
        try {
            Optional<User> existingUser = this.userRepository.findByUsername(username);
            if (existingUser.isEmpty()) {
                throw new RuntimeException("User not found for username: " + username);
            }
            UserInfo userInfo = existingUser.get().getUserInfo();;
            List<EducationalBackground> educationalBackgroundList = this.educationalRepository.findAllByUserInfoId(userInfo.getId());
            EducationalBackground educationalBackground = educationalBackgroundList.stream()
                    .filter(item -> item.getId().equals(eduId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Course item not found for ID: " + eduId));

            return (long) this.certificationsRepository.deleteByEducationalBackgroundAndId(educationalBackground, certificationId);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while trying to fetch all courses: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Long deleteOneByCourseAndId(String username, Long courseId, Long certificationId) {
        try {
            Optional<User> existingUser = this.userRepository.findByUsername(username);
            if (existingUser.isEmpty()) {
                throw new RuntimeException("User not found for username: " + username);
            }
            UserInfo userInfo = existingUser.get().getUserInfo();;
            List<Courses> coursesList = this.coursesRepository.findByUserInfo_Id(userInfo.getId());
            Courses course = coursesList.stream()
                    .filter(item -> item.getId().equals(courseId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Course item not found for ID: " + courseId));

            return (long) this.certificationsRepository.deleteByCoursesAndId(course, certificationId);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while trying to fetch all courses: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Long deleteOneByUserInfoAndId(String username, Long certificationId) {
        try {
            Optional<User> existingUser = this.userRepository.findByUsername(username);
            if (existingUser.isEmpty()) {
                throw new RuntimeException("User not found for username: " + username);
            }
            UserInfo userInfo = existingUser.get().getUserInfo();;

            return (long) this.certificationsRepository.deleteByUserInfoAndId(userInfo, certificationId);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while trying to fetch all courses: " + e.getMessage());
        }
    }
}
