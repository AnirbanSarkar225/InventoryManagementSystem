package com.inventory.ui;

import com.inventory.model.Product;
import com.inventory.model.Transaction;
import com.inventory.service.ProductService;
import com.inventory.service.TransactionService;
import com.inventory.util.ReportExporter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ReportPanel extends JPanel {

    private final ProductService productService;
    private final TransactionService transactionService;

    public ReportPanel(ProductService productService, TransactionService transactionService) {
        this.productService = productService;
        this.transactionService = transactionService;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 247, 250));

        JPanel headerLabel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerLabel.setOpaque(false);
        JLabel title = new JLabel("Reports & Export");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.add(title);
        add(headerLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(2, 2, 12, 12));
        centerPanel.setOpaque(false);
        centerPanel.add(buildReportCard("Full Inventory Report",
                "Export all products with stock levels, prices, and status to CSV.",
                new Color(52, 120, 246), this::exportInventoryCSV));
        centerPanel.add(buildReportCard("Transaction History",
                "Export all transactions (stock in, out, adjustments) to CSV.",
                new Color(40, 167, 69), this::exportTransactionsCSV));
        centerPanel.add(buildReportCard("Inventory Summary (TXT)",
                "Generate a formatted text summary report of the inventory.",
                new Color(255, 153, 0), this::exportSummaryTxt));
        centerPanel.add(buildReportCard("Low Stock Report",
                "View and export all products that are at or below reorder level.",
                new Color(220, 53, 69), this::showLowStockReport));

        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel buildReportCard(String title, String description, Color color, Runnable action) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JPanel topBar = new JPanel();
        topBar.setBackground(color);
        topBar.setPreferredSize(new Dimension(0, 5));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(40, 50, 70));

        JLabel descLabel = new JLabel("<html><p>" + description + "</p></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(new Color(100, 110, 130));

        JButton actionBtn = new JButton("Generate / Export");
        actionBtn.setBackground(color);
        actionBtn.setForeground(Color.WHITE);
        actionBtn.setFocusPainted(false);
        actionBtn.setBorderPainted(false);
        actionBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        actionBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        actionBtn.addActionListener(e -> action.run());

        JPanel textPanel = new JPanel(new BorderLayout(0, 8));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(descLabel, BorderLayout.CENTER);
        textPanel.add(actionBtn, BorderLayout.SOUTH);

        card.add(topBar, BorderLayout.NORTH);
        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    private void exportInventoryCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("inventory_report.csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                List<Product> products = productService.getAllProducts();
                ReportExporter.exportProductsToCSV(products, chooser.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Inventory CSV exported successfully.\n" + chooser.getSelectedFile().getAbsolutePath());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportTransactionsCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("transactions_report.csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                List<Transaction> transactions = transactionService.getAllTransactions();
                ReportExporter.exportTransactionsToCSV(transactions, chooser.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Transactions CSV exported successfully.\n" + chooser.getSelectedFile().getAbsolutePath());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportSummaryTxt() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("inventory_summary.txt"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                List<Product> products = productService.getAllProducts();
                ReportExporter.exportInventorySummaryToTxt(products, chooser.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Summary report exported successfully.\n" + chooser.getSelectedFile().getAbsolutePath());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showLowStockReport() {
        List<Product> lowStock = productService.getLowStockProducts();

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Low Stock Report", true);
        dialog.setSize(700, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        String[] cols = {"ID", "Name", "Category", "Current Qty", "Reorder Level", "Shortage"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Product p : lowStock) {
            model.addRow(new Object[]{
                    p.getId(), p.getName(), p.getCategory(), p.getQuantity(),
                    p.getReorderLevel(), Math.max(0, p.getReorderLevel() - p.getQuantity())
            });
        }
        JTable table = new JTable(model);
        table.setRowHeight(26);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(40, 52, 75));
        table.getTableHeader().setForeground(Color.WHITE);

        JLabel summaryLabel = new JLabel("  Total low-stock items: " + lowStock.size());
        summaryLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        summaryLabel.setForeground(new Color(220, 53, 69));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JButton exportBtn = new JButton("Export to CSV");
        exportBtn.setBackground(new Color(220, 53, 69));
        exportBtn.setForeground(Color.WHITE);
        exportBtn.setFocusPainted(false);
        exportBtn.setBorderPainted(false);
        exportBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File("low_stock_report.csv"));
            if (chooser.showSaveDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                try {
                    ReportExporter.exportProductsToCSV(lowStock, chooser.getSelectedFile().getAbsolutePath());
                    JOptionPane.showMessageDialog(dialog, "Exported successfully.");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(dialog, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        bottomPanel.add(summaryLabel, BorderLayout.WEST);
        bottomPanel.add(exportBtn, BorderLayout.EAST);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
