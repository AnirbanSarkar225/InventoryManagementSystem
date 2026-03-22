package com.inventory.model;

import java.time.LocalDateTime;

public class Transaction {

    public enum TransactionType {
        STOCK_IN, STOCK_OUT, ADJUSTMENT
    }

    private int id;
    private int productId;
    private String productName;
    private TransactionType type;
    private int quantity;
    private double unitPrice;
    private LocalDateTime timestamp;
    private String remarks;
    private String performedBy;

    public Transaction() {}

    public Transaction(int id, int productId, String productName, TransactionType type,
                       int quantity, double unitPrice, LocalDateTime timestamp,
                       String remarks, String performedBy) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.type = type;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.timestamp = timestamp;
        this.remarks = remarks;
        this.performedBy = performedBy;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }

    public double getTotalValue() {
        return quantity * unitPrice;
    }

    @Override
    public String toString() {
        return String.format("Transaction{id=%d, product='%s', type=%s, qty=%d, price=%.2f, at=%s}",
                id, productName, type, quantity, unitPrice, timestamp);
    }
}
