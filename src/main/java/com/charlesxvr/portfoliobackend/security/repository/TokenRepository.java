package com.charlesxvr.portfoliobackend.security.repository;

import com.charlesxvr.portfoliobackend.security.models.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByUserId(Long userId);
}
