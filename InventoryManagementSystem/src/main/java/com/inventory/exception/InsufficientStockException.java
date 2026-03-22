package com.inventory.exception;

public class InsufficientStockException extends InventoryException {

    private final int requested;
    private final int available;

    public InsufficientStockException(int requested, int available) {
        super(String.format("Insufficient stock. Requested: %d, Available: %d", requested, available));
        this.requested = requested;
        this.available = available;
    }

    public int getRequested() { return requested; }
    public int getAvailable() { return available; }
}
