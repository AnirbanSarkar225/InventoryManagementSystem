package com.inventory;

import com.inventory.exception.InventoryException;
import com.inventory.exception.ProductNotFoundException;
import com.inventory.model.Product;
import com.inventory.service.ProductService;
import com.inventory.util.DatabaseConnection;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductServiceTest {

    private static ProductService productService;
    private static int testProductId;

    @BeforeAll
    static void setup() {
        System.setProperty("db.url", "jdbc:sqlite::memory:");
        DatabaseConnection.initializeDatabase();
        productService = new ProductService();
        productService.setCurrentUser("test_user");
    }

    @Test
    @Order(1)
    void testAddProduct() throws InventoryException {
        Product p = new Product();
        p.setName("Test Product A");
        p.setCategory("Electronics");
        p.setQuantity(50);
        p.setPrice(199.99);
        p.setSupplier("Test Supplier");
        p.setReorderLevel(10);

        productService.addProduct(p);
        assertTrue(p.getId() > 0, "Product ID should be assigned after add");
        testProductId = p.getId();
    }

    @Test
    @Order(2)
    void testGetAllProducts() {
        List<Product> products = productService.getAllProducts();
        assertFalse(products.isEmpty(), "Product list should not be empty");
    }

    @Test
    @Order(3)
    void testGetProductById() throws ProductNotFoundException {
        Product p = productService.getProductById(testProductId);
        assertNotNull(p);
        assertEquals("Test Product A", p.getName());
        assertEquals("Electronics", p.getCategory());
        assertEquals(50, p.getQuantity());
    }

    @Test
    @Order(4)
    void testUpdateProduct() throws InventoryException {
        Product p = productService.getProductById(testProductId);
        p.setName("Updated Product A");
        p.setPrice(299.99);
        productService.updateProduct(p);

        Product updated = productService.getProductById(testProductId);
        assertEquals("Updated Product A", updated.getName());
        assertEquals(299.99, updated.getPrice(), 0.001);
    }

    @Test
    @Order(5)
    void testStockIn() throws InventoryException {
        productService.stockIn(testProductId, 20, 299.99, "Test restock");
        Product p = productService.getProductById(testProductId);
        assertEquals(70, p.getQuantity(), "Quantity should increase by 20 after stock in");
    }

    @Test
    @Order(6)
    void testStockOut() throws InventoryException {
        productService.stockOut(testProductId, 10, 350.00, "Test sale");
        Product p = productService.getProductById(testProductId);
        assertEquals(60, p.getQuantity(), "Quantity should decrease by 10 after stock out");
    }

    @Test
    @Order(7)
    void testInsufficientStockThrowsException() {
        assertThrows(InventoryException.class, () ->
                productService.stockOut(testProductId, 9999, 100.0, "Over-sell attempt")
        );
    }

    @Test
    @Order(8)
    void testAdjustStock() throws InventoryException {
        productService.adjustStock(testProductId, 100, "Audit correction");
        Product p = productService.getProductById(testProductId);
        assertEquals(100, p.getQuantity(), "Stock should be set to exactly 100 after adjustment");
    }

    @Test
    @Order(9)
    void testSearchProducts() {
        List<Product> results = productService.searchProducts("Updated");
        assertFalse(results.isEmpty(), "Search should return results for 'Updated'");
    }

    @Test
    @Order(10)
    void testLowStockDetection() throws InventoryException {
        productService.adjustStock(testProductId, 5, "Force low stock");
        List<Product> lowStock = productService.getLowStockProducts();
        assertTrue(lowStock.stream().anyMatch(p -> p.getId() == testProductId),
                "Product with qty=5 should appear in low stock list (reorder level=10)");
    }

    @Test
    @Order(11)
    void testDeleteProductThrowsWhenNotFound() {
        assertThrows(InventoryException.class, () ->
                productService.deleteProduct(99999)
        );
    }

    @Test
    @Order(12)
    void testDeleteProduct() throws InventoryException {
        productService.deleteProduct(testProductId);
        assertThrows(ProductNotFoundException.class, () ->
                productService.getProductById(testProductId)
        );
    }

    @Test
    @Order(13)
    void testAddProductWithEmptyNameThrows() {
        Product p = new Product();
        p.setName("");
        p.setCategory("Electronics");
        p.setQuantity(10);
        p.setPrice(50.0);
        p.setReorderLevel(5);
        assertThrows(Exception.class, () -> productService.addProduct(p));
    }

    @Test
    @Order(14)
    void testTotalInventoryValueIsNonNegative() {
        double value = productService.getTotalInventoryValue();
        assertTrue(value >= 0, "Total inventory value should be non-negative");
    }
}
