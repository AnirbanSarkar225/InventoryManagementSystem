package com.inventory.model;

import java.time.LocalDate;

public class Product {

    private int id;
    private String name;
    private String category;
    private int quantity;
    private double price;
    private String supplier;
    private LocalDate expiryDate;
    private int reorderLevel;

    public Product() {}

    public Product(int id, String name, String category, int quantity, double price,
                   String supplier, LocalDate expiryDate, int reorderLevel) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.price = price;
        this.supplier = supplier;
        this.expiryDate = expiryDate;
        this.reorderLevel = reorderLevel;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public int getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(int reorderLevel) { this.reorderLevel = reorderLevel; }

    public boolean isLowStock() {
        return quantity <= reorderLevel;
    }

    public boolean isExpired() {
        if (expiryDate == null) return false;
        return LocalDate.now().isAfter(expiryDate);
    }

    @Override
    public String toString() {
        return String.format("Product{id=%d, name='%s', category='%s', qty=%d, price=%.2f, supplier='%s'}",
                id, name, category, quantity, price, supplier);
    }
}
