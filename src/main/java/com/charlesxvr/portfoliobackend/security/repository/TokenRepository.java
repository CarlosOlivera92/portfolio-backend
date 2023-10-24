package com.charlesxvr.portfoliobackend.security.repository;

import com.charlesxvr.portfoliobackend.security.models.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
}
