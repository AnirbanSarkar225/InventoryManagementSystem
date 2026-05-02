package com.inventory.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.inventory.model.Category;
import com.inventory.util.DatabaseConnection;

public class CategoryDAOImpl implements CategoryDAO {

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void addCategory(Category category) {
        String sql = "INSERT INTO categories (name, description) VALUES (?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, category.getName());
            ps.setString(2, category.getDescription());
            ps.executeUpdate();
            try {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys != null && keys.next()) {
                    category.setId(keys.getInt(1));
                } else {
                    try (ResultSet rs = getConn().createStatement().executeQuery("SELECT last_insert_rowid()")) {
                        if (rs.next()) category.setId(rs.getInt(1));
                    }
                }
            } catch (SQLFeatureNotSupportedException ex) {
                try (ResultSet rs = getConn().createStatement().executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) category.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding category: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateCategory(Category category) {
        String sql = "UPDATE categories SET name=?, description=? WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, category.getName());
            ps.setString(2, category.getDescription());
            ps.setInt(3, category.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating category: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteCategory(int id) {
        String sql = "DELETE FROM categories WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting category: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Category> getCategoryById(int id) {
        String sql = "SELECT * FROM categories WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching category: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Category> getCategoryByName(String name) {
        String sql = "SELECT * FROM categories WHERE LOWER(name)=LOWER(?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching category by name: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM categories ORDER BY name";
        try (Statement stmt = getConn().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all categories: " + e.getMessage(), e);
        }
        return list;
    }

    private Category mapRow(ResultSet rs) throws SQLException {
        Category c = new Category();
        c.setId(rs.getInt("id"));
        c.setName(rs.getString("name"));
        c.setDescription(rs.getString("description"));
        return c;
    }
}
