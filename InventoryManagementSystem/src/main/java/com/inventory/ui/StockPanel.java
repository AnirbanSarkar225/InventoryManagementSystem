package com.inventory.ui;

import com.inventory.exception.InventoryException;
import com.inventory.model.Product;
import com.inventory.service.ProductService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StockPanel extends JPanel {

    private final ProductService productService;
    private DefaultTableModel tableModel;
    private JTable productTable;
    private JTextField searchField;

    public StockPanel(ProductService productService) {
        this.productService = productService;
        initUI();
        loadProducts();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 247, 250));

        add(buildToolBar(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildActionButtons(), BorderLayout.SOUTH);
    }

    private JPanel buildToolBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setOpaque(false);
        JLabel lbl = new JLabel("Search Product:");
        searchField = new JTextField(20);
        JButton searchBtn = styleBtn("Search", new Color(52, 120, 246));
        JButton clearBtn = styleBtn("Clear", new Color(108, 117, 125));
        JButton refreshBtn = styleBtn("Refresh", new Color(108, 117, 125));
        panel.add(lbl);
        panel.add(searchField);
        panel.add(searchBtn);
        panel.add(clearBtn);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(refreshBtn);
        searchBtn.addActionListener(e -> searchProducts());
        clearBtn.addActionListener(e -> { searchField.setText(""); loadProducts(); });
        refreshBtn.addActionListener(e -> loadProducts());
        searchField.addActionListener(e -> searchProducts());
        return panel;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 235)));

        String[] cols = {"ID", "Product Name", "Category", "Current Stock", "Price (INR)", "Reorder Level", "Status"};
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
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        panel.add(new JScrollPane(productTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildActionButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(new Color(245, 247, 250));

        JButton stockInBtn = styleBtn("STOCK IN (+)", new Color(40, 167, 69));
        JButton stockOutBtn = styleBtn("STOCK OUT (-)", new Color(220, 53, 69));
        JButton adjustBtn = styleBtn("ADJUST STOCK", new Color(255, 153, 0));

        stockInBtn.setPreferredSize(new Dimension(160, 38));
        stockOutBtn.setPreferredSize(new Dimension(160, 38));
        adjustBtn.setPreferredSize(new Dimension(160, 38));

        stockInBtn.addActionListener(e -> performStockIn());
        stockOutBtn.addActionListener(e -> performStockOut());
        adjustBtn.addActionListener(e -> performAdjust());

        panel.add(stockInBtn);
        panel.add(stockOutBtn);
        panel.add(adjustBtn);
        return panel;
    }

    private void loadProducts() {
        tableModel.setRowCount(0);
        for (Product p : productService.getAllProducts()) {
            tableModel.addRow(new Object[]{
                    p.getId(), p.getName(), p.getCategory(), p.getQuantity(),
                    String.format("%.2f", p.getPrice()), p.getReorderLevel(),
                    p.isExpired() ? "EXPIRED" : (p.isLowStock() ? "LOW STOCK" : "OK")
            });
        }
    }

    private void searchProducts() {
        String kw = searchField.getText().trim();
        if (kw.isEmpty()) { loadProducts(); return; }
        tableModel.setRowCount(0);
        for (Product p : productService.searchProducts(kw)) {
            tableModel.addRow(new Object[]{
                    p.getId(), p.getName(), p.getCategory(), p.getQuantity(),
                    String.format("%.2f", p.getPrice()), p.getReorderLevel(),
                    p.isExpired() ? "EXPIRED" : (p.isLowStock() ? "LOW STOCK" : "OK")
            });
        }
    }

    private int getSelectedProductId() {
        int row = productTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a product from the table.");
            return -1;
        }
        return (int) tableModel.getValueAt(row, 0);
    }

    private void performStockIn() {
        int id = getSelectedProductId();
        if (id < 0) return;
        String productName = (String) tableModel.getValueAt(productTable.getSelectedRow(), 1);

        JTextField qtyField = new JTextField("1");
        JTextField priceField = new JTextField("0.00");
        JTextField remarksField = new JTextField();

        Object[] fields = {
                "Product: " + productName,
                "Quantity to Add:", qtyField,
                "Unit Price (INR):", priceField,
                "Remarks:", remarksField
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Stock In", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            int qty = Integer.parseInt(qtyField.getText().trim());
            double price = Double.parseDouble(priceField.getText().trim());
            String remarks = remarksField.getText().trim();
            productService.stockIn(id, qty, price, remarks.isEmpty() ? "Stock In" : remarks);
            loadProducts();
            JOptionPane.showMessageDialog(this, "Stock In recorded successfully.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity or price.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (InventoryException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performStockOut() {
        int id = getSelectedProductId();
        if (id < 0) return;
        String productName = (String) tableModel.getValueAt(productTable.getSelectedRow(), 1);
        int currentStock = (int) tableModel.getValueAt(productTable.getSelectedRow(), 3);

        JTextField qtyField = new JTextField("1");
        JTextField priceField = new JTextField("0.00");
        JTextField remarksField = new JTextField();

        Object[] fields = {
                "Product: " + productName + "  (Current Stock: " + currentStock + ")",
                "Quantity to Remove:", qtyField,
                "Sale Price (INR):", priceField,
                "Remarks:", remarksField
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Stock Out", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            int qty = Integer.parseInt(qtyField.getText().trim());
            double price = Double.parseDouble(priceField.getText().trim());
            String remarks = remarksField.getText().trim();
            productService.stockOut(id, qty, price, remarks.isEmpty() ? "Stock Out" : remarks);
            loadProducts();
            JOptionPane.showMessageDialog(this, "Stock Out recorded successfully.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity or price.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (InventoryException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performAdjust() {
        int id = getSelectedProductId();
        if (id < 0) return;
        String productName = (String) tableModel.getValueAt(productTable.getSelectedRow(), 1);
        int currentStock = (int) tableModel.getValueAt(productTable.getSelectedRow(), 3);

        JTextField newQtyField = new JTextField(String.valueOf(currentStock));
        JTextField remarksField = new JTextField();

        Object[] fields = {
                "Product: " + productName + "  (Current Stock: " + currentStock + ")",
                "New Quantity:", newQtyField,
                "Reason for Adjustment:", remarksField
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Adjust Stock", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            int newQty = Integer.parseInt(newQtyField.getText().trim());
            String remarks = remarksField.getText().trim();
            productService.adjustStock(id, newQty, remarks.isEmpty() ? "Manual adjustment" : remarks);
            loadProducts();
            JOptionPane.showMessageDialog(this, "Stock adjusted successfully.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (InventoryException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton styleBtn(String text, Color color) {
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
