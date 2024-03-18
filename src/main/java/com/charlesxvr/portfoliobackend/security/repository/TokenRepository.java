package com.charlesxvr.portfoliobackend.security.repository;

import com.charlesxvr.portfoliobackend.security.models.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByUserId(Long userId);

    void deleteByToken(String token);

    Token findByToken(String token);

    Token findByUser_Id(Long id);

    @Transactional
    @Modifying
    @Query("delete from Token t where t.token = ?1")
    int delete_by_token(String token);
}
