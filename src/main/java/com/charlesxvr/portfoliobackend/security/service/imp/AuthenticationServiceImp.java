package com.charlesxvr.portfoliobackend.security.service.imp;

import com.charlesxvr.portfoliobackend.dto.UserInfoDTO;
import com.charlesxvr.portfoliobackend.javamail.EmailSender;
import com.charlesxvr.portfoliobackend.models.entities.UserInfo;
import com.charlesxvr.portfoliobackend.security.dto.*;
import com.charlesxvr.portfoliobackend.security.models.entities.RefreshToken;
import com.charlesxvr.portfoliobackend.security.models.entities.Token;
import com.charlesxvr.portfoliobackend.security.models.entities.User;
import com.charlesxvr.portfoliobackend.security.repository.TokenRepository;
import com.charlesxvr.portfoliobackend.security.repository.UserRepository;
import com.charlesxvr.portfoliobackend.security.service.AuthenticationService;
import com.charlesxvr.portfoliobackend.security.service.JwtService;
import com.charlesxvr.portfoliobackend.security.service.RefreshTokenService;
import com.charlesxvr.portfoliobackend.security.service.UserService;
import com.charlesxvr.portfoliobackend.services.UserInfoService;
import io.jsonwebtoken.Claims;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class AuthenticationServiceImp implements AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final UserInfoService userInfoService;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    @Autowired
    public AuthenticationServiceImp(
            AuthenticationManager authenticationManager,
            RefreshTokenService refreshTokenService,
            UserService userService,
            JwtService jwtService,
            TokenRepository tokenRepository,
            UserInfoService userInfoService,
            UserRepository userRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
        this.userService = userService;
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
        this.userInfoService = userInfoService;
        this.userRepository = userRepository;
    }
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    EmailSender emailSender = new EmailSender(mailSender);
    @Override
    @Transactional
    public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        // Check authentication using AuthenticationManager
        authenticationManager.authenticate(authToken);

        // Fetch User after successful authentication
        User user = userService.findByUsername(authenticationRequest.getUsername()).get();

        // Check for existing token and handle expiration
        Token existingToken = jwtService.findTokenByUserId(user.getId());
        if (existingToken != null) {
            if (jwtService.validateToken(existingToken.getToken())) {
                this.refreshTokenService.deleteByUserId(user.getId());
                RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());
                return new AuthenticationResponse(existingToken.getToken(), refreshToken.getToken(), user);
            } else {
                try {
                    String jwt = existingToken.getToken();
                    this.tokenRepository.delete_by_token(jwt);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        }
        this.refreshTokenService.deleteByUserId(user.getId());
        // Generate new JWT if no valid existing token
        Token jwt = jwtService.generateToken(user, generateExtraClaims(user));
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());
        return new AuthenticationResponse(jwt.getToken(), refreshToken.getToken(), user);
    }
    @Transactional
    public InvalidateTokenResult logout(String jwt) {
        try {
            String jwtString = jwt.split(" ")[1];

            Token token = this.tokenRepository.findByToken(jwtString);

            User user = this.userRepository.findByToken_Token(token.getToken());

            this.tokenRepository.deleteByUser(user);
            this.refreshTokenService.deleteByUserId(user.getId());
            return new InvalidateTokenResult(true,"Session closed");

        } catch (Exception e) {
            throw new RuntimeException("Error occurred trying to log out: " + e.getMessage());
        }
    }
    @Override
    @Transactional
    public UserDto register(User user) {
        User newUser = this.userService.newUser(user);
        UserInfo newUserInfo = new UserInfo();
        UserInfoDTO userInfoDto = this.userInfoService.createUserInfo(newUserInfo, user.getUsername());
        String recipientEmail = user.getEmail();
        String subject = "NoReply | New Account Created";
        String content = "<p>Hello,</p>"+ user.getFirstName() + user.getLastName() +
                "<p>Thank you for register in Solo Resume!</p>" +
                "<p>Enjoy!</p>";
        try {
            emailSender.sendEmail(recipientEmail, subject, content);
            System.out.println("Email sent successfully.");
        } catch (MessagingException | UnsupportedEncodingException e) {
            System.out.println("Failed to send email. Error: " + e.getMessage());
        }
        return new UserDto(newUser, userInfoDto);
    }
    @Override
    public boolean checkEditPermission(String token, pathUrlDto url) {
        Claims tokenClaims = jwtService.extractAllClaims(token);
        String usernameFromClaims = tokenClaims.getSubject();
        String[] segments = url.getUrl().split("/");
        String username = segments[2];
        return Objects.equals(username, usernameFromClaims);
    }
    @Override
    public Map<String, Object> generateExtraClaims(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("name", user.getFirstName());
        extraClaims.put("role", user.getRole());
        extraClaims.put("permissions", user.getAuthorities());
        return extraClaims;
    }
}
