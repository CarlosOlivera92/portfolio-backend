package com.charlesxvr.portfoliobackend.repositories;

import com.charlesxvr.portfoliobackend.models.entities.UserInfo;
import com.charlesxvr.portfoliobackend.security.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
    @Query("select u from UserInfo u where u.user.id = ?1")
    UserInfo findByUser_Id(Long id);

    @Transactional
    @Modifying
    @Query("delete from UserInfo u where u.user = ?1")
    int deleteByUser(User user);

    @Transactional
    @Modifying
    @Query("update UserInfo u set u.profilePicUrl = ?1 where u.id = ?2")
    int updateProfilePicUrlById(String profilePicUrl, Long id);

}
