package com.inventory.dao;

import com.inventory.model.Supplier;

import java.util.List;
import java.util.Optional;

public interface SupplierDAO {
    void addSupplier(Supplier supplier);
    void updateSupplier(Supplier supplier);
    void deleteSupplier(int id);
    Optional<Supplier> getSupplierById(int id);
    Optional<Supplier> getSupplierByName(String name);
    List<Supplier> getAllSuppliers();
    List<Supplier> searchSuppliers(String keyword);
}
