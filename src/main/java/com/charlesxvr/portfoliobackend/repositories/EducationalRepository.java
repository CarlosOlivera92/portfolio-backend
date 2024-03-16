package com.charlesxvr.portfoliobackend.repositories;

import com.charlesxvr.portfoliobackend.models.entities.EducationalBackground;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EducationalRepository extends JpaRepository<EducationalBackground, Long> {

    @Query("select e from EducationalBackground e where e.userInfo.id = ?1")
    EducationalBackground findByUserInfo_Id(Long id);

    @Query("select e from EducationalBackground e where e.userInfo.id = ?1")
    List<EducationalBackground> findAllByUserInfoId(Long id);
}
