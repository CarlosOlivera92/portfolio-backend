package com.charlesxvr.portfoliobackend.security.service;

import com.charlesxvr.portfoliobackend.security.models.entities.Token;
import com.charlesxvr.portfoliobackend.security.models.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getUsers();
    User getUserById(Long id);
    User newUser(User user, Token token);
    User updateUser(Long id, User user);
    Optional<User> findByUsername(String username);
    void deleteUser(Long id);
}
