package com.charlesxvr.portfoliobackend.security.repository;

import com.charlesxvr.portfoliobackend.security.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername (String username);
    User findByEmail(String email);
    Optional<User> findByResetPasswordToken(String token);

    @Transactional
    @Modifying
    @Query("delete from User u where u.username = ?1")
    int deleteByUsername(String username);

    @Query("select u from User u where u.token.token = ?1")
    User findByToken_Token(String token);
}
