package com.module.bpmn.service;

import com.module.bpmn.model.User;

import java.util.Optional;

public interface UserService {
    Optional<User> getUserByUsername(String username);
    Optional<User> getUserById(Integer id);
    void deleteUser(User user);
    User addUser(User user);
    User updateUser(User user);

    User registrationUser(User user);
}
