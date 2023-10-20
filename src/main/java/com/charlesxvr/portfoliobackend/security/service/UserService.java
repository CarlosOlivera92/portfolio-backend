package com.charlesxvr.portfoliobackend.security.service;

import com.charlesxvr.portfoliobackend.security.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getUsers();
    User getUserById(Long id);
    User newUser(User user);
    User updateUser(Long id, User user);
    Optional<User> findByUsername(String username);

    void deleteUser(Long id);
}
