package com.inventory.service;

import com.inventory.dao.ProductDAO;
import com.inventory.dao.ProductDAOImpl;
import com.inventory.dao.TransactionDAO;
import com.inventory.dao.TransactionDAOImpl;
import com.inventory.exception.InsufficientStockException;
import com.inventory.exception.InventoryException;
import com.inventory.exception.ProductNotFoundException;
import com.inventory.model.Product;
import com.inventory.model.Transaction;
import com.inventory.util.ValidationUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ProductService {

    private final ProductDAO productDAO;
    private final TransactionDAO transactionDAO;
    private String currentUser = "system";

    public ProductService() {
        this.productDAO = new ProductDAOImpl();
        this.transactionDAO = new TransactionDAOImpl();
    }

    public void setCurrentUser(String username) {
        this.currentUser = username;
    }

    public void addProduct(Product product) throws InventoryException {
        ValidationUtil.requireNonEmpty(product.getName(), "Product name");
        ValidationUtil.requireNonEmpty(product.getCategory(), "Category");
        ValidationUtil.requireNonNegativeInt(product.getQuantity(), "Quantity");
        ValidationUtil.requirePositiveDouble(product.getPrice(), "Price");

        Optional<Product> existing = productDAO.getProductByName(product.getName());
        if (existing.isPresent()) {
            throw new InventoryException("A product with name '" + product.getName() + "' already exists.");
        }

        productDAO.addProduct(product);

        if (product.getQuantity() > 0) {
            Transaction t = new Transaction();
            t.setProductId(product.getId());
            t.setProductName(product.getName());
            t.setType(Transaction.TransactionType.STOCK_IN);
            t.setQuantity(product.getQuantity());
            t.setUnitPrice(product.getPrice());
            t.setTimestamp(LocalDateTime.now());
            t.setRemarks("Initial stock on product creation");
            t.setPerformedBy(currentUser);
            transactionDAO.addTransaction(t);
        }
    }

    public void updateProduct(Product product) throws InventoryException {
        ValidationUtil.requireNonEmpty(product.getName(), "Product name");
        ValidationUtil.requireNonEmpty(product.getCategory(), "Category");
        ValidationUtil.requireNonNegativeInt(product.getQuantity(), "Quantity");
        ValidationUtil.requirePositiveDouble(product.getPrice(), "Price");

        getProductOrThrow(product.getId());
        productDAO.updateProduct(product);
    }

    public void deleteProduct(int id) throws InventoryException {
        getProductOrThrow(id);
        productDAO.deleteProduct(id);
    }

    public Product getProductById(int id) throws ProductNotFoundException {
        return getProductOrThrow(id);
    }

    public List<Product> getAllProducts() {
        return productDAO.getAllProducts();
    }

    public List<Product> getProductsByCategory(String category) {
        return productDAO.getProductsByCategory(category);
    }

    public List<Product> getLowStockProducts() {
        return productDAO.getLowStockProducts();
    }

    public List<Product> getExpiredProducts() {
        return productDAO.getExpiredProducts();
    }

    public List<Product> searchProducts(String keyword) {
        return productDAO.searchProducts(keyword);
    }

    public void stockIn(int productId, int quantity, double unitPrice, String remarks)
            throws InventoryException {
        if (quantity <= 0) throw new InventoryException("Stock-in quantity must be positive.");

        Product product = getProductOrThrow(productId);
        int newQty = product.getQuantity() + quantity;
        productDAO.updateStock(productId, newQty);

        Transaction t = new Transaction();
        t.setProductId(productId);
        t.setProductName(product.getName());
        t.setType(Transaction.TransactionType.STOCK_IN);
        t.setQuantity(quantity);
        t.setUnitPrice(unitPrice > 0 ? unitPrice : product.getPrice());
        t.setTimestamp(LocalDateTime.now());
        t.setRemarks(remarks);
        t.setPerformedBy(currentUser);
        transactionDAO.addTransaction(t);
    }

    public void stockOut(int productId, int quantity, double unitPrice, String remarks)
            throws InventoryException {
        if (quantity <= 0) throw new InventoryException("Stock-out quantity must be positive.");

        Product product = getProductOrThrow(productId);
        if (product.getQuantity() < quantity) {
            throw new InsufficientStockException(quantity, product.getQuantity());
        }

        int newQty = product.getQuantity() - quantity;
        productDAO.updateStock(productId, newQty);

        Transaction t = new Transaction();
        t.setProductId(productId);
        t.setProductName(product.getName());
        t.setType(Transaction.TransactionType.STOCK_OUT);
        t.setQuantity(quantity);
        t.setUnitPrice(unitPrice > 0 ? unitPrice : product.getPrice());
        t.setTimestamp(LocalDateTime.now());
        t.setRemarks(remarks);
        t.setPerformedBy(currentUser);
        transactionDAO.addTransaction(t);
    }

    public void adjustStock(int productId, int newQuantity, String remarks)
            throws InventoryException {
        if (newQuantity < 0) throw new InventoryException("Adjusted quantity cannot be negative.");

        Product product = getProductOrThrow(productId);
        productDAO.updateStock(productId, newQuantity);

        Transaction t = new Transaction();
        t.setProductId(productId);
        t.setProductName(product.getName());
        t.setType(Transaction.TransactionType.ADJUSTMENT);
        t.setQuantity(Math.abs(newQuantity - product.getQuantity()));
        t.setUnitPrice(product.getPrice());
        t.setTimestamp(LocalDateTime.now());
        t.setRemarks("Adjustment: " + product.getQuantity() + " -> " + newQuantity + ". " + remarks);
        t.setPerformedBy(currentUser);
        transactionDAO.addTransaction(t);
    }

    private Product getProductOrThrow(int id) throws ProductNotFoundException {
        return productDAO.getProductById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    public double getTotalInventoryValue() {
        return productDAO.getAllProducts().stream()
                .mapToDouble(p -> p.getPrice() * p.getQuantity())
                .sum();
    }
}
