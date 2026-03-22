package com.inventory.ui;

import com.inventory.service.*;
import com.inventory.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {

    private final UserService userService;
    private final ProductService productService;
    private final SupplierService supplierService;
    private final TransactionService transactionService;
    private final CategoryService categoryService;

    public MainFrame(UserService userService) {
        this.userService = userService;
        this.productService = new ProductService();
        this.supplierService = new SupplierService();
        this.transactionService = new TransactionService();
        this.categoryService = new CategoryService();

        User current = userService.getLoggedInUser();
        if (current != null) {
            productService.setCurrentUser(current.getUsername());
        }

        initUI();
    }

    private void initUI() {
        User user = userService.getLoggedInUser();
        setTitle("Inventory Management System  |  " + (user != null ? user.getFullName() + " [" + user.getRole() + "]" : ""));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1400, 850);
        setMinimumSize(new Dimension(1100, 680));
        setLocationRelativeTo(null);
        setResizable(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                        MainFrame.this,
                        "Are you sure you want to exit?",
                        "Confirm Exit",
                        JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        JPanel topBar = buildTopBar();
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        tabbedPane.addTab("  Dashboard  ", new DashboardPanel(productService, transactionService));
        tabbedPane.addTab("  Products  ", new ProductPanel(productService, categoryService));
        tabbedPane.addTab("  Stock Management  ", new StockPanel(productService));
        tabbedPane.addTab("  Suppliers  ", new SupplierPanel(supplierService));
        tabbedPane.addTab("  Transactions  ", new TransactionPanel(transactionService));
        tabbedPane.addTab("  Categories  ", new CategoryPanel(categoryService));
        tabbedPane.addTab("  Reports  ", new ReportPanel(productService, transactionService));

        if (user != null && user.getRole() == User.Role.ADMIN) {
            tabbedPane.addTab("  Users  ", new UserManagementPanel(userService));
        }

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(topBar, BorderLayout.NORTH);
        contentPane.add(tabbedPane, BorderLayout.CENTER);

        setContentPane(contentPane);
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(15, 23, 42));
        bar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel appLabel = new JLabel("📦  Inventory Management System");
        appLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        appLabel.setForeground(Color.WHITE);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightPanel.setOpaque(false);

        User user = userService.getLoggedInUser();
        if (user != null) {
            JLabel roleTag = new JLabel(" " + user.getRole() + " ");
            roleTag.setFont(new Font("Segoe UI", Font.BOLD, 11));
            roleTag.setForeground(Color.WHITE);
            roleTag.setBackground(new Color(37, 99, 235));
            roleTag.setOpaque(true);
            roleTag.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));

            JLabel userLabel = new JLabel("👤  " + user.getFullName());
            userLabel.setForeground(new Color(203, 213, 225));
            userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

            rightPanel.add(userLabel);
            rightPanel.add(roleTag);
        }

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(185, 28, 28));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        logoutBtn.addActionListener(e -> {
            userService.logout();
            dispose();
            LoginFrame lf = new LoginFrame(new UserService());
            lf.setVisible(true);
        });

        rightPanel.add(logoutBtn);
        bar.add(appLabel, BorderLayout.WEST);
        bar.add(rightPanel, BorderLayout.EAST);
        return bar;
    }
}
