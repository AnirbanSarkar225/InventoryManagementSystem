package com.inventory.ui;

import com.inventory.model.Product;
import com.inventory.model.Transaction;
import com.inventory.service.ProductService;
import com.inventory.service.TransactionService;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardPanel extends JPanel {

    private final ProductService productService;
    private final TransactionService transactionService;

    private JLabel totalProductsLabel;
    private JLabel totalValueLabel;
    private JLabel lowStockLabel;
    private JLabel expiredLabel;
    private DefaultTableModel alertTableModel;
    private DefaultTableModel recentTxModel;

    public DashboardPanel(ProductService productService, TransactionService transactionService) {
        this.productService = productService;
        this.transactionService = transactionService;
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 247, 250));

        JPanel statsPanel = buildStatsPanel();
        add(statsPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        centerPanel.setOpaque(false);
        centerPanel.add(buildAlertPanel());
        centerPanel.add(buildRecentTxPanel());
        add(centerPanel, BorderLayout.CENTER);

        JPanel refreshPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshPanel.setOpaque(false);
        JButton refreshBtn = new JButton("Refresh Dashboard");
        refreshBtn.setBackground(new Color(52, 120, 246));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setBorderPainted(false);
        refreshBtn.addActionListener(e -> loadData());
        refreshPanel.add(refreshBtn);
        add(refreshPanel, BorderLayout.SOUTH);
    }

    private JPanel buildStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 10, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        totalProductsLabel = new JLabel("0", SwingConstants.CENTER);
        totalValueLabel = new JLabel("INR 0.00", SwingConstants.CENTER);
        lowStockLabel = new JLabel("0", SwingConstants.CENTER);
        expiredLabel = new JLabel("0", SwingConstants.CENTER);

        panel.add(buildStatCard("Total Products", totalProductsLabel, new Color(52, 120, 246)));
        panel.add(buildStatCard("Inventory Value", totalValueLabel, new Color(40, 167, 69)));
        panel.add(buildStatCard("Low Stock Alerts", lowStockLabel, new Color(255, 153, 0)));
        panel.add(buildStatCard("Expired Items", expiredLabel, new Color(220, 53, 69)));

        return panel;
    }

    private JPanel buildStatCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JPanel colorBar = new JPanel();
        colorBar.setBackground(color);
        colorBar.setPreferredSize(new Dimension(0, 5));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(new Color(100, 110, 130));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);

        card.add(colorBar, BorderLayout.NORTH);
        card.add(titleLabel, BorderLayout.CENTER);
        card.add(valueLabel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel buildAlertPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235)),
                "Low Stock & Expiry Alerts",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13)));

        String[] cols = {"ID", "Product", "Category", "Qty", "Reorder Lvl", "Alert"};
        alertTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(alertTableModel);
        table.setRowHeight(24);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setSelectionBackground(new Color(200, 220, 255));

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildRecentTxPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235)),
                "Recent Transactions (Last 10)",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13)));

        String[] cols = {"Product", "Type", "Qty", "Price", "Date"};
        recentTxModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(recentTxModel);
        table.setRowHeight(24);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setSelectionBackground(new Color(200, 220, 255));

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void loadData() {
        List<Product> products = productService.getAllProducts();
        List<Product> lowStock = productService.getLowStockProducts();
        List<Product> expired = productService.getExpiredProducts();
        List<Transaction> recent = transactionService.getRecentTransactions(10);

        totalProductsLabel.setText(String.valueOf(products.size()));
        totalValueLabel.setText(String.format("INR %.0f", productService.getTotalInventoryValue()));
        lowStockLabel.setText(String.valueOf(lowStock.size()));
        expiredLabel.setText(String.valueOf(expired.size()));

        alertTableModel.setRowCount(0);
        for (Product p : lowStock) {
            alertTableModel.addRow(new Object[]{
                    p.getId(), p.getName(), p.getCategory(), p.getQuantity(), p.getReorderLevel(), "LOW STOCK"
            });
        }
        for (Product p : expired) {
            alertTableModel.addRow(new Object[]{
                    p.getId(), p.getName(), p.getCategory(), p.getQuantity(), p.getReorderLevel(), "EXPIRED"
            });
        }

        recentTxModel.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        for (Transaction t : recent) {
            recentTxModel.addRow(new Object[]{
                    t.getProductName(), t.getType(), t.getQuantity(),
                    String.format("%.2f", t.getUnitPrice()),
                    t.getTimestamp() != null ? t.getTimestamp().format(fmt) : ""
            });
        }
    }
}
