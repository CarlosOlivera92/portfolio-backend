package com.charlesxvr.portfoliobackend.repositories;

import com.charlesxvr.portfoliobackend.models.entities.Courses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoursesRepository extends JpaRepository<Courses, Long> {
    List<Courses> findByUserInfo_Id(Long id);
}
