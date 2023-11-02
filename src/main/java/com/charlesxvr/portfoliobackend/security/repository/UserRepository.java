package com.charlesxvr.portfoliobackend.security.repository;

import com.charlesxvr.portfoliobackend.security.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername (String username);
    User findByEmail(String email);
    User findByTelefono(BigDecimal telefono);
    Optional<User> findByResetPasswordToken(String token);

}
