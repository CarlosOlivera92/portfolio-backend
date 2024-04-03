package com.charlesxvr.portfoliobackend.security.service.imp;

import com.charlesxvr.portfoliobackend.dto.UserInfoDTO;
import com.charlesxvr.portfoliobackend.exceptions.TokenRefreshException;
import com.charlesxvr.portfoliobackend.exceptions.UsernameAlreadyExistsException;
import com.charlesxvr.portfoliobackend.javamail.EmailSender;
import com.charlesxvr.portfoliobackend.models.entities.UserInfo;
import com.charlesxvr.portfoliobackend.repositories.UserInfoRepository;
import com.charlesxvr.portfoliobackend.security.dto.UserDto;
import com.charlesxvr.portfoliobackend.security.dto.apiResponseDto;
import com.charlesxvr.portfoliobackend.security.models.TokenRefreshResponse;
import com.charlesxvr.portfoliobackend.security.models.entities.RefreshToken;
import com.charlesxvr.portfoliobackend.security.models.entities.Token;
import com.charlesxvr.portfoliobackend.security.models.entities.User;
import com.charlesxvr.portfoliobackend.security.repository.RefreshTokenRepository;
import com.charlesxvr.portfoliobackend.security.repository.TokenRepository;
import com.charlesxvr.portfoliobackend.security.repository.UserRepository;
import com.charlesxvr.portfoliobackend.security.service.AuthenticationService;
import com.charlesxvr.portfoliobackend.security.service.JwtService;
import com.charlesxvr.portfoliobackend.security.service.RefreshTokenService;
import com.charlesxvr.portfoliobackend.security.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserServiceImp implements UserService {
    private static final long EXPIRE_TOKEN_AFTER_MINUTES = 30;
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

    EmailSender emailSender = new EmailSender(mailSender);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private JwtService jwtService;


    @Override
    public List<UserDto> getUsers() {
        List<User> userList = this.userRepository.findAll();
        if (!userList.isEmpty()) {
            List<UserDto> userDtoList = new ArrayList<>();
            for (User user : userList) {
                UserInfoDTO userInfoDTO = new UserInfoDTO(user.getUserInfo());
                userDtoList.add(new UserDto(user, userInfoDTO));
            }
            return userDtoList;
        } else {
            return null;
        }

    }

    @Override
    public User getUserById(Long id) {
        return this.userRepository.findById(id).orElse(null);
    }
    @Override
    public User newUser(User user) {
        if (isUsernameTaken(user.getUsername())) {
            throw new UsernameAlreadyExistsException("The username is already in use");
        }
        return this.userRepository.save(user);
    }
    @Override
    public boolean isUsernameTaken(String username) {
        User existingUser = userRepository.findByUsername(username).orElse(null);
        return existingUser != null;
    }
    @Override
    public User updateUser(Long id, User user) {
        User currentUser = this.userRepository.findById(id).orElse(null);
        if(currentUser != null) {
            currentUser.setFirstName(user.getFirstName());
            currentUser.setLastName(user.getLastName());
            currentUser.setBirthday(user.getBirthday());
            currentUser.setPhoneNumber(user.getPhoneNumber());
            currentUser.setEmail(user.getEmail());
            currentUser.setUsername(user.getUsername());
            if(user.getPassword() == null) {
                currentUser.setPassword(currentUser.getPassword());
            }
            return this.userRepository.save(currentUser);
        }
        return null;
    }

    @Override
    @Transactional
    public Optional<User> findByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public Optional<UserDto> findUserAndInfoByUsername(String username) {
        try {
            Optional<User> existingUser = this.userRepository.findByUsername(username);
            if (existingUser.isEmpty()) {
                throw new RuntimeException("User not found");
            }
            User user = existingUser.get();
            UserInfo userInfo = user.getUserInfo();
            UserInfoDTO userInfoDTO = new UserInfoDTO(user.getUserInfo());
            UserDto userDto = new UserDto(user, userInfoDTO);
            return Optional.of(userDto);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByResetPasswordToken(String token) {
        return userRepository.findByResetPasswordToken(token);
    }

    @Override
    public String forgotPassword(String email) {
        User user = findByEmail(email);
        System.out.println(user);
        if (user == null) {
            return "Invalid email";
        }
        user.setResetPasswordToken(createToken());
        user.setTokenCreationDate(LocalDateTime.now());
        userRepository.save(user);
        return user.getResetPasswordToken();
    }
    @Override
    public apiResponseDto sendResponse (String response, String email) {
        if (!response.startsWith("Invalid")) {
            String subject = "NoReply | Reset Password";

            response = "http://localhost:5173/resetpassword?token=" + response;
            String content = "<p>Hello,</p>" +
                    "<p>You've requested a password change. To proceed with the process, please follow the link below:</p>" +
                    "<p><a href=\"" + response + "\">Reset Password</a></p>" +
                    "<p>Thank you!</p>";
            try {
                emailSender.sendEmail(email, subject, content);
                System.out.println("Email sent successfully.");
            } catch (MessagingException | UnsupportedEncodingException e) {
                System.out.println("Failed to send email. Error: " + e.getMessage());
            }
        }
        return new apiResponseDto(response);
    }
    @Override
    public String resetPassword(String token, String password) {
        Optional<User> userOptional = userRepository.findByResetPasswordToken(token);
        if (!userOptional.isPresent()) {
            return "Invalid token.";
        }
        LocalDateTime tokenCreationDate = userOptional.get().getTokenCreationDate();

        if(isTokenExpired(tokenCreationDate)) {
            return "token expired";
        }
        User user = userOptional.get();
        user.setPassword(password);
        user.setResetPasswordToken(null);
        user.setTokenCreationDate(null);
        userRepository.save(user);
        return "Your password successfully updated.";
    }

    @Override
    public boolean isTokenExpired(LocalDateTime tokenCreationDate) {
        LocalDateTime now = LocalDateTime.now();
        Duration diff = Duration.between(tokenCreationDate, now);

        return diff.toMinutes() >= EXPIRE_TOKEN_AFTER_MINUTES;
    }
    private String createToken() {
        String token = UUID.randomUUID().toString();
        return token;
    }
    @Override
    @Transactional
    public void deleteUser(String username) {
        try {
            Optional<User> existingUser = this.userRepository.findByUsername(username);
            if (existingUser.isEmpty()) {
                throw new RuntimeException("No se ha encontrado al usuario");
            }
            this.refreshTokenRepository.deleteByUserId(existingUser.get().getId());
            this.tokenRepository.deleteByUser(existingUser.get());
            userInfoRepository.deleteByUser(existingUser.get());
            this.userRepository.deleteByUsername(existingUser.get().getUsername());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public TokenRefreshResponse refhreshToken(String refreshToken) {
        try {
            Optional<RefreshToken> storedRefreshToken = refreshTokenService.findByToken(refreshToken);
            if (storedRefreshToken.isEmpty()) {
                throw new RuntimeException("Error: Requested refresh token not in database");
            }
            Optional<RefreshToken> verifiedToken = refreshTokenService.verifyExpiration(storedRefreshToken);
            if (verifiedToken.isEmpty()) {
                throw new RuntimeException("Invalid token");
            }
            User tokenOwner = verifiedToken.get().getUser();
            Token userToken = tokenOwner.getToken();
            if (userToken == null) {
                Token newToken = jwtService.generateToken(tokenOwner, generateExtraClaims(tokenOwner));
                this.refreshTokenService.deleteByUserId(tokenOwner.getId());
                RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(tokenOwner.getId());
                return new TokenRefreshResponse(newToken.getToken(), newRefreshToken.getToken());
            }
            this.jwtService.invalidateToken(userToken.getToken());
            Token newToken = jwtService.generateToken(tokenOwner, generateExtraClaims(tokenOwner));
            this.refreshTokenService.deleteByUserId(tokenOwner.getId());
            RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(tokenOwner.getId());
            return new TokenRefreshResponse(newToken.getToken(), newRefreshToken.getToken());

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    private Map<String, Object> generateExtraClaims(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("name", user.getFirstName());
        extraClaims.put("role", user.getRole());
        extraClaims.put("permissions", user.getAuthorities());
        return extraClaims;
    }
}
