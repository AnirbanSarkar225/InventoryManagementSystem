package com.inventory.dao;

import com.inventory.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDAO {
    void addUser(User user);
    void updateUser(User user);
    void deleteUser(int id);
    Optional<User> getUserById(int id);
    Optional<User> getUserByUsername(String username);
    List<User> getAllUsers();
    boolean usernameExists(String username);
}
