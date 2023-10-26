package com.charlesxvr.portfoliobackend.security.service.imp;

import com.charlesxvr.portfoliobackend.exceptions.UsernameAlreadyExistsException;
import com.charlesxvr.portfoliobackend.security.models.entities.Token;
import com.charlesxvr.portfoliobackend.security.models.entities.User;
import com.charlesxvr.portfoliobackend.security.repository.UserRepository;
import com.charlesxvr.portfoliobackend.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImp implements UserService {
    private static final long EXPIRE_TOKEN_AFTER_MINUTES = 30;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return this.userRepository.findById(id).orElse(null);
    }


    @Override
    public User newUser(User user, Token token) {
        user.setToken(token);
        if (isUsernameTaken(user.getUsername())) {
            throw new UsernameAlreadyExistsException("The username is already in use");
        }
        return this.userRepository.save(user);
    }
    private boolean isUsernameTaken(String username) {
        // Verificar si el nombre de usuario ya existe en la base de datos
        User existingUser = userRepository.findByUsername(username).orElse(null);
        return existingUser != null;
    }
    @Override
    public User updateUser(Long id, User user) {
        User currentUser = this.userRepository.findById(id).orElse(null);
        if(currentUser != null) {
            currentUser.setFirstName(user.getFirstName());
            currentUser.setLastName(user.getLastName());
            currentUser.setAboutMe(user.getAboutMe());
            currentUser.setBirthday(user.getBirthday());
            currentUser.setPhoneNumber(user.getPhoneNumber());
            currentUser.setProfilePicUrl(user.getProfilePicUrl());
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
