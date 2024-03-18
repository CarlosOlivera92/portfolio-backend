package com.charlesxvr.portfoliobackend.repositories;

import com.charlesxvr.portfoliobackend.models.entities.Certifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificationsRepository extends JpaRepository<Certifications, Long> {

    @Query("select c from Certifications c where c.educationalBackground.id = ?1")
    List<Certifications> findAllByEducationalBackground_Id(Long id);

    @Query("select c from Certifications c where c.courses.id = ?1")
    List<Certifications> findAllByCourses_Id(Long id);

    @Query("select c from Certifications c where c.userInfo.id = ?1")
    List<Certifications> findAllByUserInfo_Id(Long id);

    @Query("select c from Certifications c where c.educationalBackground.id = ?1 and c.id = ?2")
    Certifications findByEducationalBackground_IdAndId(Long id, Long id1);

    @Query("select c from Certifications c where c.courses.id = ?1 and c.id = ?2")
    Certifications findByCourses_IdAndId(Long id, Long id1);

    @Query("select c from Certifications c where c.userInfo.id = ?1 and c.id = ?2")
    Certifications findByUserInfo_IdAndId(Long id, Long id1);
}
