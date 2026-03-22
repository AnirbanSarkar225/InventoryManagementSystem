package com.inventory.ui;

import com.inventory.exception.InventoryException;
import com.inventory.model.Supplier;
import com.inventory.service.SupplierService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SupplierPanel extends JPanel {

    private final SupplierService supplierService;
    private DefaultTableModel tableModel;
    private JTable supplierTable;
    private JTextField searchField;

    public SupplierPanel(SupplierService supplierService) {
        this.supplierService = supplierService;
        initUI();
        loadSuppliers();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 247, 250));
        add(buildToolBar(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
    }

    private JPanel buildToolBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        left.setOpaque(false);
        left.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        left.add(searchField);
        JButton searchBtn = btn("Search", new Color(52, 120, 246));
        JButton clearBtn = btn("Clear", new Color(108, 117, 125));
        left.add(searchBtn);
        left.add(clearBtn);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        right.setOpaque(false);
        JButton addBtn = btn("+ Add Supplier", new Color(40, 167, 69));
        JButton editBtn = btn("Edit", new Color(255, 153, 0));
        JButton deleteBtn = btn("Delete", new Color(220, 53, 69));
        JButton refreshBtn = btn("Refresh", new Color(108, 117, 125));
        right.add(addBtn);
        right.add(editBtn);
        right.add(deleteBtn);
        right.add(refreshBtn);

        searchBtn.addActionListener(e -> searchSuppliers());
        clearBtn.addActionListener(e -> { searchField.setText(""); loadSuppliers(); });
        searchField.addActionListener(e -> searchSuppliers());
        addBtn.addActionListener(e -> openDialog(null));
        editBtn.addActionListener(e -> editSelected());
        deleteBtn.addActionListener(e -> deleteSelected());
        refreshBtn.addActionListener(e -> loadSuppliers());

        panel.add(left, BorderLayout.WEST);
        panel.add(right, BorderLayout.EAST);
        return panel;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 235)));

        String[] cols = {"ID", "Name", "Contact Person", "Phone", "Email", "Address"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        supplierTable = new JTable(tableModel);
        supplierTable.setRowHeight(26);
        supplierTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        supplierTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        supplierTable.getTableHeader().setBackground(new Color(40, 52, 75));
        supplierTable.getTableHeader().setForeground(Color.WHITE);
        supplierTable.setSelectionBackground(new Color(200, 220, 255));
        supplierTable.setGridColor(new Color(230, 235, 245));
        supplierTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) editSelected();
            }
        });

        panel.add(new JScrollPane(supplierTable), BorderLayout.CENTER);
        return panel;
    }

    private void loadSuppliers() {
        tableModel.setRowCount(0);
        for (Supplier s : supplierService.getAllSuppliers()) {
            tableModel.addRow(new Object[]{s.getId(), s.getName(), s.getContactPerson(), s.getPhone(), s.getEmail(), s.getAddress()});
        }
    }

    private void searchSuppliers() {
        String kw = searchField.getText().trim();
        if (kw.isEmpty()) { loadSuppliers(); return; }
        tableModel.setRowCount(0);
        for (Supplier s : supplierService.searchSuppliers(kw)) {
            tableModel.addRow(new Object[]{s.getId(), s.getName(), s.getContactPerson(), s.getPhone(), s.getEmail(), s.getAddress()});
        }
    }

    private void editSelected() {
        int row = supplierTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Please select a supplier."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        try {
            openDialog(supplierService.getSupplierById(id));
        } catch (InventoryException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = supplierTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Please select a supplier."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete supplier '" + name + "'?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            supplierService.deleteSupplier(id);
            loadSuppliers();
            JOptionPane.showMessageDialog(this, "Supplier deleted.");
        } catch (InventoryException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openDialog(Supplier existing) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                existing == null ? "Add Supplier" : "Edit Supplier", true);
        dialog.setSize(420, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 5, 6, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameF = new JTextField(20);
        JTextField contactF = new JTextField(20);
        JTextField phoneF = new JTextField(20);
        JTextField emailF = new JTextField(20);
        JTextField addressF = new JTextField(20);

        if (existing != null) {
            nameF.setText(existing.getName());
            contactF.setText(existing.getContactPerson() != null ? existing.getContactPerson() : "");
            phoneF.setText(existing.getPhone() != null ? existing.getPhone() : "");
            emailF.setText(existing.getEmail() != null ? existing.getEmail() : "");
            addressF.setText(existing.getAddress() != null ? existing.getAddress() : "");
        }

        String[] labels = {"Name *", "Contact Person", "Phone", "Email", "Address"};
        JTextField[] fields = {nameF, contactF, phoneF, emailF, addressF};
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0.3;
            form.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1; gbc.weightx = 0.7;
            form.add(fields[i], gbc);
        }

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = btn("Save", new Color(40, 167, 69));
        JButton cancelBtn = btn("Cancel", new Color(108, 117, 125));
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        saveBtn.addActionListener(e -> {
            try {
                Supplier s = existing != null ? existing : new Supplier();
                s.setName(nameF.getText().trim());
                s.setContactPerson(contactF.getText().trim());
                s.setPhone(phoneF.getText().trim());
                s.setEmail(emailF.getText().trim());
                s.setAddress(addressF.getText().trim());
                if (existing == null) supplierService.addSupplier(s);
                else supplierService.updateSupplier(s);
                loadSuppliers();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Supplier saved successfully.");
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
