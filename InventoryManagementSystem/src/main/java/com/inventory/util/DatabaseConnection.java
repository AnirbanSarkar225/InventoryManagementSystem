package com.inventory.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:sqlite:inventory.db";
    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            connection.setAutoCommit(true);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Failed to initialize database connection: " + e.getMessage(), e);
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null || !instance.isConnected()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    public static void initializeDatabase() {
        DatabaseConnection db = getInstance();
        try (Statement stmt = db.getConnection().createStatement()) {

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS categories (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL UNIQUE,
                        description TEXT
                    )
                    """);

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS suppliers (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        contact_person TEXT,
                        phone TEXT,
                        email TEXT,
                        address TEXT
                    )
                    """);

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS products (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        category TEXT NOT NULL,
                        quantity INTEGER NOT NULL DEFAULT 0,
                        price REAL NOT NULL DEFAULT 0.0,
                        supplier TEXT,
                        expiry_date TEXT,
                        reorder_level INTEGER NOT NULL DEFAULT 10
                    )
                    """);

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS transactions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        product_id INTEGER NOT NULL,
                        product_name TEXT NOT NULL,
                        type TEXT NOT NULL,
                        quantity INTEGER NOT NULL,
                        unit_price REAL NOT NULL DEFAULT 0.0,
                        timestamp TEXT NOT NULL,
                        remarks TEXT,
                        performed_by TEXT,
                        FOREIGN KEY (product_id) REFERENCES products(id)
                    )
                    """);

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        username TEXT NOT NULL UNIQUE,
                        password_hash TEXT NOT NULL,
                        full_name TEXT NOT NULL,
                        role TEXT NOT NULL DEFAULT 'STAFF',
                        active INTEGER NOT NULL DEFAULT 1
                    )
                    """);

            db.seedDefaultData(stmt);

        } catch (SQLException e) {
            throw new RuntimeException("Database initialization failed: " + e.getMessage(), e);
        }
    }

    private void seedDefaultData(Statement stmt) throws SQLException {
        stmt.execute("""
                INSERT OR IGNORE INTO users (username, password_hash, full_name, role, active)
                VALUES ('admin', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'Administrator', 'ADMIN', 1)
                """);

        stmt.execute("INSERT OR IGNORE INTO categories (name, description) VALUES ('Electronics', 'Electronic devices and components')");
        stmt.execute("INSERT OR IGNORE INTO categories (name, description) VALUES ('Stationery', 'Office and writing supplies')");
        stmt.execute("INSERT OR IGNORE INTO categories (name, description) VALUES ('Furniture', 'Office and home furniture')");
        stmt.execute("INSERT OR IGNORE INTO categories (name, description) VALUES ('Food & Beverage', 'Consumable food and drink items')");
        stmt.execute("INSERT OR IGNORE INTO categories (name, description) VALUES ('Clothing', 'Apparel and accessories')");
    }
}
