package com.inventory.ui;

import com.inventory.model.Transaction;
import com.inventory.service.TransactionService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TransactionPanel extends JPanel {

    private final TransactionService transactionService;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterBox;

    public TransactionPanel(TransactionService transactionService) {
        this.transactionService = transactionService;
        initUI();
        loadTransactions();
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

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);
        left.add(new JLabel("Filter by Type:"));
        filterBox = new JComboBox<>(new String[]{"ALL", "STOCK_IN", "STOCK_OUT", "ADJUSTMENT"});
        filterBox.addActionListener(e -> loadTransactions());
        left.add(filterBox);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        right.setOpaque(false);
        JButton refreshBtn = btn("Refresh", new Color(52, 120, 246));
        refreshBtn.addActionListener(e -> loadTransactions());
        right.add(refreshBtn);

        panel.add(left, BorderLayout.WEST);
        panel.add(right, BorderLayout.EAST);
        return panel;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 235)));

        String[] cols = {"ID", "Product", "Type", "Qty", "Unit Price", "Total Value", "Date & Time", "Remarks", "By"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(26);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(40, 52, 75));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(200, 220, 255));
        table.setGridColor(new Color(230, 235, 245));
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(3).setPreferredWidth(50);
        table.getColumnModel().getColumn(4).setPreferredWidth(90);
        table.getColumnModel().getColumn(5).setPreferredWidth(90);
        table.getColumnModel().getColumn(6).setPreferredWidth(140);
        table.getColumnModel().getColumn(7).setPreferredWidth(160);
        table.getColumnModel().getColumn(8).setPreferredWidth(80);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void loadTransactions() {
        tableModel.setRowCount(0);
        List<Transaction> list;
        String filter = (String) filterBox.getSelectedItem();
        if ("ALL".equals(filter)) {
            list = transactionService.getAllTransactions();
        } else {
            list = transactionService.getTransactionsByType(Transaction.TransactionType.valueOf(filter));
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        for (Transaction t : list) {
            tableModel.addRow(new Object[]{
                    t.getId(), t.getProductName(), t.getType(), t.getQuantity(),
                    String.format("%.2f", t.getUnitPrice()),
                    String.format("%.2f", t.getTotalValue()),
                    t.getTimestamp() != null ? t.getTimestamp().format(fmt) : "",
                    t.getRemarks(), t.getPerformedBy()
            });
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
