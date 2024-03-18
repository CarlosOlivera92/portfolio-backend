package com.charlesxvr.portfoliobackend.repositories;

import com.charlesxvr.portfoliobackend.models.entities.Projects;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectsRepository extends JpaRepository<Projects, Long> {

    @Query("select p from Projects p where p.userInfo.id = ?1")
    List<Projects> findByUserInfo_Id(Long id);

    @Query("select p from Projects p where p.userInfo.id = ?1")
    Projects findOneByUserInfo_id(Long id);
}
