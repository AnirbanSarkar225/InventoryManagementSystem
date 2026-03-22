package com.inventory.service;

import com.inventory.dao.SupplierDAO;
import com.inventory.dao.SupplierDAOImpl;
import com.inventory.exception.InventoryException;
import com.inventory.model.Supplier;
import com.inventory.util.ValidationUtil;

import java.util.List;
import java.util.Optional;

public class SupplierService {

    private final SupplierDAO supplierDAO;

    public SupplierService() {
        this.supplierDAO = new SupplierDAOImpl();
    }

    public void addSupplier(Supplier supplier) throws InventoryException {
        ValidationUtil.requireNonEmpty(supplier.getName(), "Supplier name");

        Optional<Supplier> existing = supplierDAO.getSupplierByName(supplier.getName());
        if (existing.isPresent()) {
            throw new InventoryException("Supplier with name '" + supplier.getName() + "' already exists.");
        }

        supplierDAO.addSupplier(supplier);
    }

    public void updateSupplier(Supplier supplier) throws InventoryException {
        ValidationUtil.requireNonEmpty(supplier.getName(), "Supplier name");
        getSupplierOrThrow(supplier.getId());
        supplierDAO.updateSupplier(supplier);
    }

    public void deleteSupplier(int id) throws InventoryException {
        getSupplierOrThrow(id);
        supplierDAO.deleteSupplier(id);
    }

    public Supplier getSupplierById(int id) throws InventoryException {
        return getSupplierOrThrow(id);
    }

    public List<Supplier> getAllSuppliers() {
        return supplierDAO.getAllSuppliers();
    }

    public List<Supplier> searchSuppliers(String keyword) {
        return supplierDAO.searchSuppliers(keyword);
    }

    private Supplier getSupplierOrThrow(int id) throws InventoryException {
        return supplierDAO.getSupplierById(id)
                .orElseThrow(() -> new InventoryException("Supplier with ID " + id + " not found."));
    }
}
