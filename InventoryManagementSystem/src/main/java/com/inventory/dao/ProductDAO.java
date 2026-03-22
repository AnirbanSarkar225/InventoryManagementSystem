package com.inventory.dao;

import com.inventory.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductDAO {
    void addProduct(Product product);
    void updateProduct(Product product);
    void deleteProduct(int id);
    Optional<Product> getProductById(int id);
    Optional<Product> getProductByName(String name);
    List<Product> getAllProducts();
    List<Product> getProductsByCategory(String category);
    List<Product> getLowStockProducts();
    List<Product> getExpiredProducts();
    List<Product> searchProducts(String keyword);
    boolean updateStock(int productId, int newQuantity);
}
