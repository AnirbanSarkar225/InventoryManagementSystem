package com.inventory;

import com.inventory.exception.AuthenticationException;
import com.inventory.exception.InventoryException;
import com.inventory.model.User;
import com.inventory.service.UserService;
import com.inventory.util.DatabaseConnection;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTest {

    private static UserService userService;

    @BeforeAll
    static void setup() {
        DatabaseConnection.initializeDatabase();
        userService = new UserService();
    }

    @Test
    @Order(1)
    void testDefaultAdminLogin() throws AuthenticationException {
        User user = userService.login("admin", "admin");
        assertNotNull(user);
        assertEquals("admin", user.getUsername());
        assertEquals(User.Role.ADMIN, user.getRole());
    }

    @Test
    @Order(2)
    void testInvalidLoginThrows() {
        assertThrows(AuthenticationException.class, () ->
                userService.login("admin", "wrongpassword")
        );
    }

    @Test
    @Order(3)
    void testIsLoggedIn() throws AuthenticationException {
        userService.login("admin", "admin");
        assertTrue(userService.isLoggedIn());
    }

    @Test
    @Order(4)
    void testLogout() throws AuthenticationException {
        userService.login("admin", "admin");
        userService.logout();
        assertFalse(userService.isLoggedIn());
    }

    @Test
    @Order(5)
    void testAddUser() throws InventoryException {
        User newUser = new User();
        newUser.setUsername("teststaff");
        newUser.setFullName("Test Staff Member");
        newUser.setRole(User.Role.STAFF);
        newUser.setActive(true);
        userService.addUser(newUser, "password123");
        assertTrue(newUser.getId() > 0, "New user should have an assigned ID");
    }

    @Test
    @Order(6)
    void testDuplicateUsernameThrows() {
        User duplicate = new User();
        duplicate.setUsername("teststaff");
        duplicate.setFullName("Another Staff");
        duplicate.setRole(User.Role.STAFF);
        duplicate.setActive(true);
        assertThrows(InventoryException.class, () -> userService.addUser(duplicate, "pass456"));
    }

    @Test
    @Order(7)
    void testNewUserCanLogin() throws AuthenticationException {
        User user = userService.login("teststaff", "password123");
        assertNotNull(user);
        assertEquals(User.Role.STAFF, user.getRole());
    }

    @Test
    @Order(8)
    void testHasRoleCheck() throws AuthenticationException {
        userService.login("admin", "admin");
        assertTrue(userService.hasRole(User.Role.ADMIN));
        assertTrue(userService.hasRole(User.Role.MANAGER));
        assertTrue(userService.hasRole(User.Role.STAFF));
    }

    @Test
    @Order(9)
    void testEmptyPasswordThrows() {
        User u = new User();
        u.setUsername("nopassuser");
        u.setFullName("No Pass User");
        u.setRole(User.Role.STAFF);
        u.setActive(true);
        assertThrows(Exception.class, () -> userService.addUser(u, ""));
    }
}
