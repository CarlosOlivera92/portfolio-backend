package com.charlesxvr.portfoliobackend.security.controllers;
import com.charlesxvr.portfoliobackend.exceptions.TokenRefreshException;
import com.charlesxvr.portfoliobackend.javamail.EmailSender;
import com.charlesxvr.portfoliobackend.security.dto.*;
import com.charlesxvr.portfoliobackend.security.models.JwtResponse;
import com.charlesxvr.portfoliobackend.security.models.TokenRefreshRequest;
import com.charlesxvr.portfoliobackend.security.models.TokenRefreshResponse;
import com.charlesxvr.portfoliobackend.security.models.entities.RefreshToken;
import com.charlesxvr.portfoliobackend.security.models.entities.Token;
import com.charlesxvr.portfoliobackend.security.models.entities.User;
import com.charlesxvr.portfoliobackend.security.service.imp.AuthenticationServiceImp;
import com.charlesxvr.portfoliobackend.security.service.imp.JwtServiceImp;
import com.charlesxvr.portfoliobackend.security.service.imp.RefreshTokenServiceImp;
import com.charlesxvr.portfoliobackend.security.service.imp.UserServiceImp;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequestMapping("api/auth")
@CrossOrigin(origins = "http://localhost:5173/")

public class AuthController {
    @Autowired
    private UserServiceImp userServiceImp;
    @Autowired
    private JwtServiceImp jwtServiceImp;
    @Autowired
    private AuthenticationServiceImp authenticationServiceImp;
    @Autowired
    private RefreshTokenServiceImp refreshTokenServiceImp;
    @Autowired
    private PasswordEncoder passwordEncoder;
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

    EmailSender emailSender = new EmailSender(mailSender);
    @PreAuthorize("permitAll")
    @PostMapping("/forgotpassword")
    public String forgotPassword(@RequestBody ForgotPasswordRequest request) {
        System.out.println(request.getEmail());
        String response = userServiceImp.forgotPassword(request.getEmail());
        if (!response.startsWith("Invalid")) {
            String recipientEmail = request.getEmail();
            String subject = "NoReply | Reset Password";

            response = "http://localhost:8080/reset-password?token=" + response;
            String content = "<p>Hello,</p>" +
                    "<p>You've requested a password change. To proceed with the process, please follow the link below:</p>" +
                    "<p><a href=\"" + response + "\">Reset Password</a></p>" +
                    "<p>Thank you!</p>";
            try {
                emailSender.sendEmail(recipientEmail, subject, content);
                System.out.println("Email sent successfully.");
            } catch (MessagingException | UnsupportedEncodingException e) {
                System.out.println("Failed to send email. Error: " + e.getMessage());
            }
        }
        return response;
    }
    @PutMapping("/reset-password")
    public String resetPassword(@RequestParam String token,
                                @RequestBody ResetPasswordRequest newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword.getNewPassword());
        return userServiceImp.resetPassword(token, encodedPassword);
    }
    @PreAuthorize("permitAll")
    @PostMapping("/signin")
    public ResponseEntity<?> login
            (@RequestBody @Valid AuthenticationRequest authRequest) {
        AuthenticationResponse jwtDto = authenticationServiceImp.login(authRequest);
        return ResponseEntity.ok(new JwtResponse(jwtDto.getJwt(),jwtDto.getRefreshToken(),jwtDto.getUser().getId(), jwtDto.getUser().getUsername(), jwtDto.getUser().getEmail(), (List<?>) jwtDto.getUser().getAuthorities()));
    }
    @PreAuthorize("permitAll")
    @PostMapping("/signup")
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Token token = this.jwtServiceImp.generateToken(user, authenticationServiceImp.generateExtraClaims(user));
        this.userServiceImp.newUser(user, token);
        UserDto userResponse = new UserDto(user);
        String recipientEmail = user.getEmail();
        String subject = "NoReply | Reset Password";
        String content = "<p>Hello,</p>"+ user.getFirstName() + user.getLastName() +
                "<p>Thank you for register in Solo Resume!</p>" +
                "<p>Thank you!</p>";
        try {
            emailSender.sendEmail(recipientEmail, subject, content);
            System.out.println("Email sent successfully.");
        } catch (MessagingException | UnsupportedEncodingException e) {
            System.out.println("Failed to send email. Error: " + e.getMessage());
        }
        return ResponseEntity.ok(userResponse);
    }
    @PreAuthorize("permitAll")
    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request){
        String requestRefreshToken = request.getRefreshToken();
        return refreshTokenServiceImp.findByToken(requestRefreshToken)
                .map(refreshTokenServiceImp::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    Token token = jwtServiceImp.generateToken(user, authenticationServiceImp.generateExtraClaims(user));
                    return ResponseEntity.ok(new TokenRefreshResponse(token.getToken(), requestRefreshToken));
                }).orElseThrow( ()  -> new TokenRefreshException(requestRefreshToken, "Refresh token not in database"));
    }
}
