package com.inventory.dao;

import com.inventory.model.Transaction;
import com.inventory.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionDAOImpl implements TransactionDAO {

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void addTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (product_id, product_name, type, quantity, unit_price, timestamp, remarks, performed_by) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, transaction.getProductId());
            ps.setString(2, transaction.getProductName());
            ps.setString(3, transaction.getType().name());
            ps.setInt(4, transaction.getQuantity());
            ps.setDouble(5, transaction.getUnitPrice());
            ps.setString(6, transaction.getTimestamp() != null ? transaction.getTimestamp().toString() : LocalDateTime.now().toString());
            ps.setString(7, transaction.getRemarks());
            ps.setString(8, transaction.getPerformedBy());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) transaction.setId(keys.getInt(1));
        } catch (SQLException e) {
            throw new RuntimeException("Error adding transaction: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Transaction> getTransactionById(int id) {
        String sql = "SELECT * FROM transactions WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching transaction: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY timestamp DESC";
        try (Statement stmt = getConn().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all transactions: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Transaction> getTransactionsByProduct(int productId) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE product_id=? ORDER BY timestamp DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching transactions by product: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Transaction> getTransactionsByType(Transaction.TransactionType type) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE type=? ORDER BY timestamp DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, type.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching transactions by type: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Transaction> getTransactionsBetween(LocalDateTime from, LocalDateTime to) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE timestamp BETWEEN ? AND ? ORDER BY timestamp DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, from.toString());
            ps.setString(2, to.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching transactions by date range: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Transaction> getRecentTransactions(int limit) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY timestamp DESC LIMIT ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching recent transactions: " + e.getMessage(), e);
        }
        return list;
    }

    private Transaction mapRow(ResultSet rs) throws SQLException {
        Transaction t = new Transaction();
        t.setId(rs.getInt("id"));
        t.setProductId(rs.getInt("product_id"));
        t.setProductName(rs.getString("product_name"));
        t.setType(Transaction.TransactionType.valueOf(rs.getString("type")));
        t.setQuantity(rs.getInt("quantity"));
        t.setUnitPrice(rs.getDouble("unit_price"));
        String ts = rs.getString("timestamp");
        if (ts != null) t.setTimestamp(LocalDateTime.parse(ts));
        t.setRemarks(rs.getString("remarks"));
        t.setPerformedBy(rs.getString("performed_by"));
        return t;
    }
}
