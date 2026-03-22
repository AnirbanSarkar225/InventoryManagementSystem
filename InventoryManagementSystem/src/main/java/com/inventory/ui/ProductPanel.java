package com.inventory.ui;

import com.inventory.exception.InventoryException;
import com.inventory.model.Category;
import com.inventory.model.Product;
import com.inventory.service.CategoryService;
import com.inventory.service.ProductService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ProductPanel extends JPanel {

    private final ProductService productService;
    private final CategoryService categoryService;

    private DefaultTableModel tableModel;
    private JTable productTable;
    private JTextField searchField;

    public ProductPanel(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
        initUI();
        loadProducts();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 247, 250));

        add(buildToolBar(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
    }

    private JPanel buildToolBar() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        searchPanel.setOpaque(false);
        JLabel searchLbl = new JLabel("Search:");
        searchField = new JTextField(20);
        JButton searchBtn = createButton("Search", new Color(52, 120, 246));
        JButton clearBtn = createButton("Clear", new Color(108, 117, 125));
        searchPanel.add(searchLbl);
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(clearBtn);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        actionPanel.setOpaque(false);
        JButton addBtn = createButton("+ Add Product", new Color(40, 167, 69));
        JButton editBtn = createButton("Edit", new Color(255, 153, 0));
        JButton deleteBtn = createButton("Delete", new Color(220, 53, 69));
        JButton refreshBtn = createButton("Refresh", new Color(108, 117, 125));
        actionPanel.add(addBtn);
        actionPanel.add(editBtn);
        actionPanel.add(deleteBtn);
        actionPanel.add(refreshBtn);

        searchBtn.addActionListener(e -> searchProducts());
        clearBtn.addActionListener(e -> { searchField.setText(""); loadProducts(); });
        addBtn.addActionListener(e -> openProductDialog(null));
        editBtn.addActionListener(e -> editSelected());
        deleteBtn.addActionListener(e -> deleteSelected());
        refreshBtn.addActionListener(e -> loadProducts());
        searchField.addActionListener(e -> searchProducts());

        panel.add(searchPanel, BorderLayout.WEST);
        panel.add(actionPanel, BorderLayout.EAST);
        return panel;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 235)));

        String[] cols = {"ID", "Name", "Category", "Quantity", "Price (INR)", "Supplier", "Expiry Date", "Reorder Lvl", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        productTable = new JTable(tableModel);
        productTable.setRowHeight(26);
        productTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        productTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        productTable.getTableHeader().setBackground(new Color(40, 52, 75));
        productTable.getTableHeader().setForeground(Color.WHITE);
        productTable.setSelectionBackground(new Color(200, 220, 255));
        productTable.setGridColor(new Color(230, 235, 245));
        productTable.setShowGrid(true);
        productTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        productTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        productTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        productTable.getColumnModel().getColumn(2).setPreferredWidth(110);
        productTable.getColumnModel().getColumn(3).setPreferredWidth(70);
        productTable.getColumnModel().getColumn(4).setPreferredWidth(90);
        productTable.getColumnModel().getColumn(5).setPreferredWidth(120);
        productTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        productTable.getColumnModel().getColumn(7).setPreferredWidth(85);
        productTable.getColumnModel().getColumn(8).setPreferredWidth(80);
        productTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) editSelected();
            }
        });

        panel.add(new JScrollPane(productTable), BorderLayout.CENTER);

        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBackground(new Color(245, 247, 250));
        panel.add(statusBar, BorderLayout.SOUTH);

        return panel;
    }

    private void loadProducts() {
        tableModel.setRowCount(0);
        for (Product p : productService.getAllProducts()) {
            tableModel.addRow(productToRow(p));
        }
    }

    private void searchProducts() {
        String kw = searchField.getText().trim();
        if (kw.isEmpty()) { loadProducts(); return; }
        tableModel.setRowCount(0);
        for (Product p : productService.searchProducts(kw)) {
            tableModel.addRow(productToRow(p));
        }
    }

    private Object[] productToRow(Product p) {
        return new Object[]{
                p.getId(), p.getName(), p.getCategory(), p.getQuantity(),
                String.format("%.2f", p.getPrice()), p.getSupplier(),
                p.getExpiryDate() != null ? p.getExpiryDate().toString() : "N/A",
                p.getReorderLevel(),
                p.isExpired() ? "EXPIRED" : (p.isLowStock() ? "LOW STOCK" : "OK")
        };
    }

    private void editSelected() {
        int row = productTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Please select a product."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        try {
            Product p = productService.getProductById(id);
            openProductDialog(p);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = productTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Please select a product."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete product '" + name + "'? This cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            productService.deleteProduct(id);
            loadProducts();
            JOptionPane.showMessageDialog(this, "Product deleted successfully.");
        } catch (InventoryException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openProductDialog(Product existing) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                existing == null ? "Add Product" : "Edit Product", true);
        dialog.setSize(450, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;

        JTextField nameField = new JTextField(20);
        List<Category> categories = categoryService.getAllCategories();
        String[] catNames = categories.stream().map(Category::getName).toArray(String[]::new);
        JComboBox<String> categoryBox = new JComboBox<>(catNames);
        JTextField quantityField = new JTextField("0");
        JTextField priceField = new JTextField("0.00");
        JTextField supplierField = new JTextField();
        JTextField expiryField = new JTextField("YYYY-MM-DD");
        JTextField reorderField = new JTextField("10");

        if (existing != null) {
            nameField.setText(existing.getName());
            categoryBox.setSelectedItem(existing.getCategory());
            quantityField.setText(String.valueOf(existing.getQuantity()));
            priceField.setText(String.valueOf(existing.getPrice()));
            supplierField.setText(existing.getSupplier() != null ? existing.getSupplier() : "");
            expiryField.setText(existing.getExpiryDate() != null ? existing.getExpiryDate().toString() : "");
            reorderField.setText(String.valueOf(existing.getReorderLevel()));
        }

        String[] labels = {"Name *", "Category *", "Quantity *", "Price (INR) *", "Supplier", "Expiry Date", "Reorder Level *"};
        Component[] fields = {nameField, categoryBox, quantityField, priceField, supplierField, expiryField, reorderField};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0.3;
            form.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1; gbc.weightx = 0.7;
            form.add(fields[i], gbc);
        }

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = createButton("Save", new Color(40, 167, 69));
        JButton cancelBtn = createButton("Cancel", new Color(108, 117, 125));
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        saveBtn.addActionListener(e -> {
            try {
                Product p = existing != null ? existing : new Product();
                p.setName(nameField.getText().trim());
                p.setCategory(categoryBox.getSelectedItem() != null ? categoryBox.getSelectedItem().toString() : "");
                p.setQuantity(Integer.parseInt(quantityField.getText().trim()));
                p.setPrice(Double.parseDouble(priceField.getText().trim()));
                p.setSupplier(supplierField.getText().trim());
                String expiry = expiryField.getText().trim();
                if (!expiry.isEmpty() && !expiry.equals("YYYY-MM-DD")) {
                    p.setExpiryDate(LocalDate.parse(expiry));
                } else {
                    p.setExpiryDate(null);
                }
                p.setReorderLevel(Integer.parseInt(reorderField.getText().trim()));

                if (existing == null) {
                    productService.addProduct(p);
                    JOptionPane.showMessageDialog(dialog, "Product added successfully.");
                } else {
                    productService.updateProduct(p);
                    JOptionPane.showMessageDialog(dialog, "Product updated successfully.");
                }
                loadProducts();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid numeric value entered.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid date format. Use YYYY-MM-DD.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (InventoryException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.add(new JScrollPane(form), BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
