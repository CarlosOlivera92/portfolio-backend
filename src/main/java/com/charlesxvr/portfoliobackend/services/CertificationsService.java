package com.charlesxvr.portfoliobackend.services;

import com.charlesxvr.portfoliobackend.models.entities.Certifications;
import com.charlesxvr.portfoliobackend.models.entities.Courses;
import com.charlesxvr.portfoliobackend.models.entities.UserInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CertificationsService {
    //CREATE NEW CERTIFICATION FOR USER INFO
    Certifications createCertification(Certifications certification, String username);
    //CREATE NEW CERTIFICATION BY EDUCATIONAL
    Certifications createCertificationByEducationalId(Certifications certification, String username, Long eduId);

    //CREATE NEW CERTIFICATION BY COURSE
    Certifications createCertificationByCourseId(Certifications certification, String username, Long courseId);

    //UPDATE CERTIFICATION BASED BY ID AND EDUCATIONAL ID
    Certifications updateCertificationByEducation(Certifications certification, Long eduId, String username, Long certificationId);

    //UPDATE CERTIFICATION BY ID AND COURSE ID
    Certifications updateCertificationByCourse(Certifications certification, Long courseId, String username, Long certificationId);

    //UPDATE CERTIFICATION BY ID AND USER INFO ID
    Certifications updateCertificationByUserInfo(Certifications certification, String username, Long certificationId);

    //GET ALL CERTIFICATIONS BY USER INFO ID
    List<Certifications> getAllByUserInfo(String username);
    //GET ALL CERTIFICATIONS BY EDUCATIONAL ID
    List<Certifications> getAllByEducationalId(Long educationalId, String username);
    //GET ALL CERTIFICATIONS BY COURSE ID
    List<Certifications> getAllByCourseId(Long courseId, String username);
    //DELETE ONE BY ID AND EDUCATIONAL ID
    Long deleteOneByEducationalAndId(String username, Long eduId, Long certificationId);
    //DELETE ONE BY ID AND COURSE ID
    Long deleteOneByCourseAndId(String username, Long courseId, Long certificationId);
    //DELETE ONE BY ID AND USER INFO ID
    Long deleteOneByUserInfoAndId(String username, Long certificationId);

}
