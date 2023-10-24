package com.charlesxvr.portfoliobackend.security.service.imp;

import com.charlesxvr.portfoliobackend.exceptions.UsernameAlreadyExistsException;
import com.charlesxvr.portfoliobackend.security.models.Token;
import com.charlesxvr.portfoliobackend.security.models.User;
import com.charlesxvr.portfoliobackend.security.repository.UserRepository;
import com.charlesxvr.portfoliobackend.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImp implements UserService {

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
    public void deleteUser(Long id) {
        this.userRepository.deleteById(id);
    }
}
