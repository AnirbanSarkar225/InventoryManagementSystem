package com.inventory.dao;

import com.inventory.model.Product;
import com.inventory.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDAOImpl implements ProductDAO {

    private Connection getConn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void addProduct(Product product) {
        String sql = "INSERT INTO products (name, category, quantity, price, supplier, expiry_date, reorder_level) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, product.getName());
            ps.setString(2, product.getCategory());
            ps.setInt(3, product.getQuantity());
            ps.setDouble(4, product.getPrice());
            ps.setString(5, product.getSupplier());
            ps.setString(6, product.getExpiryDate() != null ? product.getExpiryDate().toString() : null);
            ps.setInt(7, product.getReorderLevel());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                product.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding product: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateProduct(Product product) {
        String sql = "UPDATE products SET name=?, category=?, quantity=?, price=?, supplier=?, expiry_date=?, reorder_level=? WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.setString(2, product.getCategory());
            ps.setInt(3, product.getQuantity());
            ps.setDouble(4, product.getPrice());
            ps.setString(5, product.getSupplier());
            ps.setString(6, product.getExpiryDate() != null ? product.getExpiryDate().toString() : null);
            ps.setInt(7, product.getReorderLevel());
            ps.setInt(8, product.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating product: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteProduct(int id) {
        String sql = "DELETE FROM products WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting product: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Product> getProductById(int id) {
        String sql = "SELECT * FROM products WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching product: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Product> getProductByName(String name) {
        String sql = "SELECT * FROM products WHERE LOWER(name)=LOWER(?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching product by name: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY name";
        try (Statement stmt = getConn().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all products: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE LOWER(category)=LOWER(?) ORDER BY name";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, category);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching products by category: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Product> getLowStockProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE quantity <= reorder_level ORDER BY quantity ASC";
        try (Statement stmt = getConn().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching low stock products: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Product> getExpiredProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE expiry_date IS NOT NULL AND expiry_date < date('now') ORDER BY expiry_date";
        try (Statement stmt = getConn().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching expired products: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE LOWER(name) LIKE ? OR LOWER(category) LIKE ? OR LOWER(supplier) LIKE ?";
        String like = "%" + keyword.toLowerCase() + "%";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error searching products: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public boolean updateStock(int productId, int newQuantity) {
        String sql = "UPDATE products SET quantity=? WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, newQuantity);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating stock: " + e.getMessage(), e);
        }
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setCategory(rs.getString("category"));
        p.setQuantity(rs.getInt("quantity"));
        p.setPrice(rs.getDouble("price"));
        p.setSupplier(rs.getString("supplier"));
        String expiry = rs.getString("expiry_date");
        if (expiry != null && !expiry.isEmpty()) p.setExpiryDate(LocalDate.parse(expiry));
        p.setReorderLevel(rs.getInt("reorder_level"));
        return p;
    }
}
