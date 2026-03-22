package com.inventory.exception;

public class ProductNotFoundException extends InventoryException {

    public ProductNotFoundException(int productId) {
        super("Product with ID " + productId + " not found.");
    }

    public ProductNotFoundException(String productName) {
        super("Product '" + productName + "' not found.");
    }
}
