package com.inventory;
import com.inventory.service.UserService;
import com.inventory.ui.LoginFrame;
import com.inventory.util.DatabaseConnection;
import javax.swing.*;
public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {}
        }
        DatabaseConnection.initializeDatabase();
        SwingUtilities.invokeLater(() -> {
            UserService userService = new UserService();
            LoginFrame loginFrame = new LoginFrame(userService);
            loginFrame.setVisible(true);
        });
    }
}