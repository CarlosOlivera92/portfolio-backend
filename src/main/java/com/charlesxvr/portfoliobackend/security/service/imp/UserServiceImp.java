package com.charlesxvr.portfoliobackend.security.service.imp;

import com.charlesxvr.portfoliobackend.exceptions.UsernameAlreadyExistsException;
import com.charlesxvr.portfoliobackend.javamail.EmailSender;
import com.charlesxvr.portfoliobackend.security.dto.UserDto;
import com.charlesxvr.portfoliobackend.security.dto.apiResponseDto;
import com.charlesxvr.portfoliobackend.security.models.entities.User;
import com.charlesxvr.portfoliobackend.security.repository.TokenRepository;
import com.charlesxvr.portfoliobackend.security.repository.UserRepository;
import com.charlesxvr.portfoliobackend.security.service.UserService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImp implements UserService {
    private static final long EXPIRE_TOKEN_AFTER_MINUTES = 30;
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

    EmailSender emailSender = new EmailSender(mailSender);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;

    @Override
    public List<UserDto> getUsers() {
        List<User> userList = this.userRepository.findAll();
        if (!userList.isEmpty()) {
            List<UserDto> userDtoList = new ArrayList<>();
            for (User user : userList) {
                userDtoList.add(new UserDto(user));
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
    public Optional<User> findByUsername(String username) {
        return this.userRepository.findByUsername(username);
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
    public void deleteUser(Long id) {
        this.userRepository.deleteById(id);
    }
}
