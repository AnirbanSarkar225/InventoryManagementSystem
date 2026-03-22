package com.inventory.dao;

import com.inventory.model.Supplier;
import com.inventory.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SupplierDAOImpl implements SupplierDAO {

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void addSupplier(Supplier supplier) {
        String sql = "INSERT INTO suppliers (name, contact_person, phone, email, address) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, supplier.getName());
            ps.setString(2, supplier.getContactPerson());
            ps.setString(3, supplier.getPhone());
            ps.setString(4, supplier.getEmail());
            ps.setString(5, supplier.getAddress());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) supplier.setId(keys.getInt(1));
        } catch (SQLException e) {
            throw new RuntimeException("Error adding supplier: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateSupplier(Supplier supplier) {
        String sql = "UPDATE suppliers SET name=?, contact_person=?, phone=?, email=?, address=? WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, supplier.getName());
            ps.setString(2, supplier.getContactPerson());
            ps.setString(3, supplier.getPhone());
            ps.setString(4, supplier.getEmail());
            ps.setString(5, supplier.getAddress());
            ps.setInt(6, supplier.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating supplier: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteSupplier(int id) {
        String sql = "DELETE FROM suppliers WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting supplier: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Supplier> getSupplierById(int id) {
        String sql = "SELECT * FROM suppliers WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching supplier: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Supplier> getSupplierByName(String name) {
        String sql = "SELECT * FROM suppliers WHERE LOWER(name)=LOWER(?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching supplier by name: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Supplier> getAllSuppliers() {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT * FROM suppliers ORDER BY name";
        try (Statement stmt = getConn().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all suppliers: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Supplier> searchSuppliers(String keyword) {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT * FROM suppliers WHERE LOWER(name) LIKE ? OR LOWER(contact_person) LIKE ? OR LOWER(email) LIKE ?";
        String like = "%" + keyword.toLowerCase() + "%";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error searching suppliers: " + e.getMessage(), e);
        }
        return list;
    }

    private Supplier mapRow(ResultSet rs) throws SQLException {
        Supplier s = new Supplier();
        s.setId(rs.getInt("id"));
        s.setName(rs.getString("name"));
        s.setContactPerson(rs.getString("contact_person"));
        s.setPhone(rs.getString("phone"));
        s.setEmail(rs.getString("email"));
        s.setAddress(rs.getString("address"));
        return s;
    }
}
