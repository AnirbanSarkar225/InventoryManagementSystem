package com.inventory.ui;

import com.inventory.exception.AuthenticationException;
import com.inventory.service.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginFrame extends JFrame {

    private final UserService userService;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;

    public LoginFrame(UserService userService) {
        this.userService = userService;
        initUI();
    }

    private void initUI() {
        setTitle("Inventory Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 580);
        setMinimumSize(new Dimension(700, 480));
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel root = new JPanel(new GridLayout(1, 2));
        root.add(buildLeftPanel());
        root.add(buildRightPanel());

        setContentPane(root);
    }

    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(15, 23, 42));

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setOpaque(false);
        inner.setBorder(new EmptyBorder(40, 50, 40, 50));

        JLabel icon = new JLabel("📦");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 56));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("IMS");
        title.setFont(new Font("Segoe UI", Font.BOLD, 42));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Inventory Management");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(new Color(148, 163, 184));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle2 = new JLabel("System");
        subtitle2.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle2.setForeground(new Color(148, 163, 184));
        subtitle2.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(51, 65, 85));
        sep.setMaximumSize(new Dimension(200, 2));
        sep.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[] features = {"✔  Track Products & Stock", "✔  Manage Suppliers", "✔  Transaction History", "✔  Reports & Export"};
        inner.add(icon);
        inner.add(Box.createVerticalStrut(16));
        inner.add(title);
        inner.add(Box.createVerticalStrut(6));
        inner.add(subtitle);
        inner.add(subtitle2);
        inner.add(Box.createVerticalStrut(24));
        inner.add(sep);
        inner.add(Box.createVerticalStrut(24));
        for (String f : features) {
            JLabel fl = new JLabel(f);
            fl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            fl.setForeground(new Color(100, 180, 255));
            fl.setAlignmentX(Component.CENTER_ALIGNMENT);
            inner.add(fl);
            inner.add(Box.createVerticalStrut(8));
        }

        panel.add(inner);
        return panel;
    }

    private JPanel buildRightPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(248, 250, 252));

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(20, 50, 20, 50));
        form.setMaximumSize(new Dimension(420, 600));

        JLabel welcome = new JLabel("Welcome back");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcome.setForeground(new Color(15, 23, 42));
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Sign in to your account to continue");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sub.setForeground(new Color(100, 116, 139));
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        form.add(welcome);
        form.add(Box.createVerticalStrut(6));
        form.add(sub);
        form.add(Box.createVerticalStrut(36));

        form.add(fieldLabel("Username"));
        form.add(Box.createVerticalStrut(6));
        usernameField = new JTextField();
        styleInputField(usernameField);
        form.add(usernameField);

        form.add(Box.createVerticalStrut(18));
        form.add(fieldLabel("Password"));
        form.add(Box.createVerticalStrut(6));
        passwordField = new JPasswordField();
        styleInputField(passwordField);
        form.add(passwordField);

        form.add(Box.createVerticalStrut(10));
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(220, 53, 69));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(statusLabel);

        form.add(Box.createVerticalStrut(10));
        JButton loginBtn = new JButton("Sign In");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        loginBtn.setBackground(new Color(37, 99, 235));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.addActionListener(this::handleLogin);
        loginBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { loginBtn.setBackground(new Color(29, 78, 216)); }
            public void mouseExited(MouseEvent e) { loginBtn.setBackground(new Color(37, 99, 235)); }
        });
        form.add(loginBtn);

        form.add(Box.createVerticalStrut(20));
        JLabel hint = new JLabel("Default credentials:  admin / admin");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        hint.setForeground(new Color(148, 163, 184));
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(hint);

        panel.add(form);
        getRootPane().setDefaultButton(loginBtn);
        return panel;
    }

    private JLabel fieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(51, 65, 85));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private void styleInputField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(Color.WHITE);
        field.setForeground(new Color(15, 23, 42));
        field.setCaretColor(new Color(37, 99, 235));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void handleLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter username and password.");
            return;
        }

        try {
            userService.login(username, password);
            dispose();
            MainFrame mainFrame = new MainFrame(userService);
            mainFrame.setVisible(true);
        } catch (AuthenticationException ex) {
            statusLabel.setText(ex.getMessage());
            passwordField.setText("");
        }
    }
}
