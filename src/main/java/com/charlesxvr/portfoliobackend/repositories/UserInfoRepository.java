package com.charlesxvr.portfoliobackend.repositories;

import com.charlesxvr.portfoliobackend.models.entities.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
    @Query("select u from UserInfo u where u.user.id = ?1")
    UserInfo findByUser_Id(Long id);

}
