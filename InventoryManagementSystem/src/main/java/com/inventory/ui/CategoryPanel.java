package com.inventory.ui;

import com.inventory.exception.InventoryException;
import com.inventory.model.Category;
import com.inventory.service.CategoryService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CategoryPanel extends JPanel {

    private final CategoryService categoryService;
    private DefaultTableModel tableModel;
    private JTable categoryTable;

    public CategoryPanel(CategoryService categoryService) {
        this.categoryService = categoryService;
        initUI();
        loadCategories();
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
        JButton addBtn = btn("+ Add Category", new Color(40, 167, 69));
        JButton editBtn = btn("Edit", new Color(255, 153, 0));
        JButton deleteBtn = btn("Delete", new Color(220, 53, 69));
        JButton refreshBtn = btn("Refresh", new Color(108, 117, 125));
        panel.add(addBtn);
        panel.add(editBtn);
        panel.add(deleteBtn);
        panel.add(refreshBtn);
        addBtn.addActionListener(e -> openDialog(null));
        editBtn.addActionListener(e -> editSelected());
        deleteBtn.addActionListener(e -> deleteSelected());
        refreshBtn.addActionListener(e -> loadCategories());
        return panel;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 235)));

        String[] cols = {"ID", "Category Name", "Description"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        categoryTable = new JTable(tableModel);
        categoryTable.setRowHeight(28);
        categoryTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        categoryTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        categoryTable.getTableHeader().setBackground(new Color(40, 52, 75));
        categoryTable.getTableHeader().setForeground(Color.WHITE);
        categoryTable.setSelectionBackground(new Color(200, 220, 255));
        categoryTable.setGridColor(new Color(230, 235, 245));
        categoryTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        categoryTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        categoryTable.getColumnModel().getColumn(2).setPreferredWidth(400);
        categoryTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) editSelected();
            }
        });

        panel.add(new JScrollPane(categoryTable), BorderLayout.CENTER);
        return panel;
    }

    private void loadCategories() {
        tableModel.setRowCount(0);
        for (Category c : categoryService.getAllCategories()) {
            tableModel.addRow(new Object[]{c.getId(), c.getName(), c.getDescription()});
        }
    }

    private void editSelected() {
        int row = categoryTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Please select a category."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        Category c = new Category(id,
                (String) tableModel.getValueAt(row, 1),
                (String) tableModel.getValueAt(row, 2));
        openDialog(c);
    }

    private void deleteSelected() {
        int row = categoryTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Please select a category."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete category '" + name + "'?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            categoryService.deleteCategory(id);
            loadCategories();
            JOptionPane.showMessageDialog(this, "Category deleted.");
        } catch (InventoryException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openDialog(Category existing) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                existing == null ? "Add Category" : "Edit Category", true);
        dialog.setSize(380, 230);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameF = new JTextField(20);
        JTextField descF = new JTextField(20);

        if (existing != null) {
            nameF.setText(existing.getName());
            descF.setText(existing.getDescription() != null ? existing.getDescription() : "");
        }

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3; form.add(new JLabel("Name *:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7; form.add(nameF, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3; form.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7; form.add(descF, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = btn("Save", new Color(40, 167, 69));
        JButton cancelBtn = btn("Cancel", new Color(108, 117, 125));
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        saveBtn.addActionListener(e -> {
            try {
                Category c = existing != null ? existing : new Category();
                c.setName(nameF.getText().trim());
                c.setDescription(descF.getText().trim());
                if (existing == null) categoryService.addCategory(c);
                else categoryService.updateCategory(c);
                loadCategories();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Category saved.");
            } catch (InventoryException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
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
