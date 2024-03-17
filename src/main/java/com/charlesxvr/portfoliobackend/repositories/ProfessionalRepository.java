package com.charlesxvr.portfoliobackend.repositories;

import com.charlesxvr.portfoliobackend.models.entities.EducationalBackground;
import com.charlesxvr.portfoliobackend.models.entities.ProfessionalBackground;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ProfessionalRepository extends JpaRepository<ProfessionalBackground, Long> {

    @Query("select p from ProfessionalBackground p where p.userInfo.id = ?1")
    List<ProfessionalBackground> findByUserInfo_Id(Long id);
}
