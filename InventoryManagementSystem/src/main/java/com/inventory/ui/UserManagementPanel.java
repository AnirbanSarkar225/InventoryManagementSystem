package com.inventory.ui;

import com.inventory.exception.InventoryException;
import com.inventory.model.User;
import com.inventory.service.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserManagementPanel extends JPanel {

    private final UserService userService;
    private DefaultTableModel tableModel;
    private JTable userTable;

    public UserManagementPanel(UserService userService) {
        this.userService = userService;
        initUI();
        loadUsers();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 247, 250));
        add(buildToolBar(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
    }

    private JPanel buildToolBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setOpaque(false);
        JButton addBtn = btn("+ Add User", new Color(40, 167, 69));
        JButton editBtn = btn("Edit", new Color(255, 153, 0));
        JButton changePwdBtn = btn("Change Password", new Color(52, 120, 246));
        JButton toggleBtn = btn("Toggle Active", new Color(108, 117, 125));
        JButton deleteBtn = btn("Delete", new Color(220, 53, 69));
        JButton refreshBtn = btn("Refresh", new Color(108, 117, 125));
        panel.add(addBtn);
        panel.add(editBtn);
        panel.add(changePwdBtn);
        panel.add(toggleBtn);
        panel.add(deleteBtn);
        panel.add(refreshBtn);
        addBtn.addActionListener(e -> openAddDialog());
        editBtn.addActionListener(e -> editSelected());
        changePwdBtn.addActionListener(e -> changePasswordSelected());
        toggleBtn.addActionListener(e -> toggleActive());
        deleteBtn.addActionListener(e -> deleteSelected());
        refreshBtn.addActionListener(e -> loadUsers());
        return panel;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 235)));

        String[] cols = {"ID", "Username", "Full Name", "Role", "Active"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        userTable = new JTable(tableModel);
        userTable.setRowHeight(28);
        userTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        userTable.getTableHeader().setBackground(new Color(40, 52, 75));
        userTable.getTableHeader().setForeground(Color.WHITE);
        userTable.setSelectionBackground(new Color(200, 220, 255));
        userTable.setGridColor(new Color(230, 235, 245));

        panel.add(new JScrollPane(userTable), BorderLayout.CENTER);
        return panel;
    }

    private void loadUsers() {
        tableModel.setRowCount(0);
        for (User u : userService.getAllUsers()) {
            tableModel.addRow(new Object[]{u.getId(), u.getUsername(), u.getFullName(), u.getRole(), u.isActive() ? "Yes" : "No"});
        }
    }

    private int getSelectedId() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user.");
            return -1;
        }
        return (int) tableModel.getValueAt(row, 0);
    }

    private void openAddDialog() {
        JTextField usernameF = new JTextField(15);
        JPasswordField passwordF = new JPasswordField(15);
        JTextField nameF = new JTextField(15);
        JComboBox<User.Role> roleBox = new JComboBox<>(User.Role.values());

        Object[] fields = {"Username *:", usernameF, "Password *:", passwordF, "Full Name *:", nameF, "Role:", roleBox};
        int result = JOptionPane.showConfirmDialog(this, fields, "Add New User", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            User user = new User();
            user.setUsername(usernameF.getText().trim());
            user.setFullName(nameF.getText().trim());
            user.setRole((User.Role) roleBox.getSelectedItem());
            user.setActive(true);
            userService.addUser(user, new String(passwordF.getPassword()));
            loadUsers();
            JOptionPane.showMessageDialog(this, "User added successfully.");
        } catch (InventoryException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSelected() {
        int id = getSelectedId();
        if (id < 0) return;
        int row = userTable.getSelectedRow();
        String currentName = (String) tableModel.getValueAt(row, 2);
        String currentRole = tableModel.getValueAt(row, 3).toString();

        JTextField nameF = new JTextField(currentName, 15);
        JComboBox<User.Role> roleBox = new JComboBox<>(User.Role.values());
        roleBox.setSelectedItem(User.Role.valueOf(currentRole));

        Object[] fields = {"Full Name *:", nameF, "Role:", roleBox};
        int result = JOptionPane.showConfirmDialog(this, fields, "Edit User", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            List<User> users = userService.getAllUsers();
            User user = users.stream().filter(u -> u.getId() == id).findFirst().orElse(null);
            if (user == null) { JOptionPane.showMessageDialog(this, "User not found."); return; }
            user.setFullName(nameF.getText().trim());
            user.setRole((User.Role) roleBox.getSelectedItem());
            userService.updateUser(user);
            loadUsers();
            JOptionPane.showMessageDialog(this, "User updated successfully.");
        } catch (InventoryException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void changePasswordSelected() {
        int id = getSelectedId();
        if (id < 0) return;
        JPasswordField newPwd = new JPasswordField(15);
        int result = JOptionPane.showConfirmDialog(this, new Object[]{"New Password:", newPwd},
                "Change Password", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;
        try {
            userService.changePassword(id, new String(newPwd.getPassword()));
            JOptionPane.showMessageDialog(this, "Password changed successfully.");
        } catch (InventoryException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void toggleActive() {
        int id = getSelectedId();
        if (id < 0) return;
        try {
            List<User> users = userService.getAllUsers();
            User user = users.stream().filter(u -> u.getId() == id).findFirst().orElse(null);
            if (user == null) return;
            user.setActive(!user.isActive());
            userService.updateUser(user);
            loadUsers();
            JOptionPane.showMessageDialog(this, "User status updated to: " + (user.isActive() ? "Active" : "Inactive"));
        } catch (InventoryException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int id = getSelectedId();
        if (id < 0) return;
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this user? This cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            userService.deleteUser(id);
            loadUsers();
            JOptionPane.showMessageDialog(this, "User deleted.");
        } catch (InventoryException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton btn(String text, Color color) {
        JButton b = new JButton(text);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}
