package com.charlesxvr.portfoliobackend.security.service;

import com.charlesxvr.portfoliobackend.security.models.entities.Token;
import com.charlesxvr.portfoliobackend.security.models.entities.User;
import org.springframework.cglib.core.Local;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getUsers();
    User getUserById(Long id);
    User newUser(User user);
    User updateUser(Long id, User user);
    Optional<User> findByUsername(String username);
    User findByEmail(String email);
    Optional<User> findByResetPasswordToken(String token);
    String forgotPassword(String email);
    String resetPassword(String token, String password);
    boolean isTokenExpired(LocalDateTime tokenCreationDate);
    void deleteUser(Long id);
}
