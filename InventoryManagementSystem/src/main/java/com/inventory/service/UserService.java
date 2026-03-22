package com.inventory.service;

import com.inventory.dao.UserDAO;
import com.inventory.dao.UserDAOImpl;
import com.inventory.exception.AuthenticationException;
import com.inventory.exception.InventoryException;
import com.inventory.model.User;
import com.inventory.util.PasswordUtil;
import com.inventory.util.ValidationUtil;

import java.util.List;

public class UserService {

    private final UserDAO userDAO;
    private User loggedInUser;

    public UserService() {
        this.userDAO = new UserDAOImpl();
    }

    public User login(String username, String password) throws AuthenticationException {
        if (ValidationUtil.isNullOrEmpty(username) || ValidationUtil.isNullOrEmpty(password)) {
            throw new AuthenticationException("Username and password are required.");
        }

        User user = userDAO.getUserByUsername(username)
                .orElseThrow(() -> new AuthenticationException("Invalid username or password."));

        if (!user.isActive()) {
            throw new AuthenticationException("Account is disabled. Contact administrator.");
        }

        if (!PasswordUtil.verify(password, user.getPasswordHash())) {
            throw new AuthenticationException("Invalid username or password.");
        }

        this.loggedInUser = user;
        return user;
    }

    public void logout() {
        this.loggedInUser = null;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public boolean isLoggedIn() {
        return loggedInUser != null;
    }

    public void addUser(User user, String plainPassword) throws InventoryException {
        ValidationUtil.requireNonEmpty(user.getUsername(), "Username");
        ValidationUtil.requireNonEmpty(plainPassword, "Password");
        ValidationUtil.requireNonEmpty(user.getFullName(), "Full name");

        if (userDAO.usernameExists(user.getUsername())) {
            throw new InventoryException("Username '" + user.getUsername() + "' already exists.");
        }

        user.setPasswordHash(PasswordUtil.hash(plainPassword));
        userDAO.addUser(user);
    }

    public void updateUser(User user) throws InventoryException {
        ValidationUtil.requireNonEmpty(user.getUsername(), "Username");
        ValidationUtil.requireNonEmpty(user.getFullName(), "Full name");
        getUserOrThrow(user.getId());
        userDAO.updateUser(user);
    }

    public void changePassword(int userId, String newPassword) throws InventoryException {
        ValidationUtil.requireNonEmpty(newPassword, "New password");
        User user = getUserOrThrow(userId);
        user.setPasswordHash(PasswordUtil.hash(newPassword));
        userDAO.updateUser(user);
    }

    public void deleteUser(int id) throws InventoryException {
        getUserOrThrow(id);
        userDAO.deleteUser(id);
    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    private User getUserOrThrow(int id) throws InventoryException {
        return userDAO.getUserById(id)
                .orElseThrow(() -> new InventoryException("User with ID " + id + " not found."));
    }

    public boolean hasRole(User.Role required) {
        if (loggedInUser == null) return false;
        return loggedInUser.getRole().ordinal() <= required.ordinal();
    }
}
