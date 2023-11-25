package com.charlesxvr.portfoliobackend.security.service.imp;

import com.charlesxvr.portfoliobackend.javamail.EmailSender;
import com.charlesxvr.portfoliobackend.security.dto.*;
import com.charlesxvr.portfoliobackend.security.models.entities.RefreshToken;
import com.charlesxvr.portfoliobackend.security.models.entities.Token;
import com.charlesxvr.portfoliobackend.security.models.entities.User;
import com.charlesxvr.portfoliobackend.security.service.AuthenticationService;
import io.jsonwebtoken.Claims;
import jakarta.mail.MessagingException;
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
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RefreshTokenServiceImp refreshTokenServiceImp;
    @Autowired
    private UserServiceImp userServiceImp;
    @Autowired
    private JwtServiceImp jwtServiceImp;
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    EmailSender emailSender = new EmailSender(mailSender);
    public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        authenticationManager.authenticate(authToken);
        User user = userServiceImp.findByUsername(authenticationRequest.getUsername()).get();
        Token jwt = jwtServiceImp.generateToken(user, generateExtraClaims(user));
        this.jwtServiceImp.saveToken(jwt);
        RefreshToken refreshToken = refreshTokenServiceImp.createRefreshToken(user.getId());
        return new AuthenticationResponse(jwt.getToken(), refreshToken.getToken(), user);
    }
    public UserDto register(User user) {
        User newUser = this.userServiceImp.newUser(user);
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
        return new UserDto(newUser);
    }
    public boolean checkEditPermission(String token, pathUrlDto url) {
        Claims tokenClaims = jwtServiceImp.extractAllClaims(token);
        String usernameFromClaims = tokenClaims.getSubject();
        String[] segments = url.getUrl().split("/");
        String username = segments[2];
        return Objects.equals(username, usernameFromClaims);
    }
    public Map<String, Object> generateExtraClaims(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("name", user.getFirstName());
        extraClaims.put("role", user.getRole());
        extraClaims.put("permissions", user.getAuthorities());
        return extraClaims;
    }
}
