package com.charlesxvr.portfoliobackend.security.service;

import com.charlesxvr.portfoliobackend.security.dto.UserDto;
import com.charlesxvr.portfoliobackend.security.dto.apiResponseDto;
import com.charlesxvr.portfoliobackend.security.models.TokenRefreshResponse;
import com.charlesxvr.portfoliobackend.security.models.entities.RefreshToken;
import com.charlesxvr.portfoliobackend.security.models.entities.Token;
import com.charlesxvr.portfoliobackend.security.models.entities.User;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Service
public interface UserService {
    apiResponseDto sendResponse (String response, String email);
    boolean isUsernameTaken(String username);
    List<UserDto> getUsers();
    User getUserById(Long id);
    User newUser(User user);
    User updateUser(Long id, User user);
    Optional<User> findByUsername(String username);
    Optional<UserDto> findUserAndInfoByUsername(String username);
    User findByEmail(String email);
    Optional<User> findByResetPasswordToken(String token);
    String forgotPassword(String email);
    String resetPassword(String token, String password);
    boolean isTokenExpired(LocalDateTime tokenCreationDate);
    void deleteUser(String username);
    TokenRefreshResponse refhreshToken (String refreshToken);
}
