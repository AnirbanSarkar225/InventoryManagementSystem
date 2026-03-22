package com.inventory;

import com.inventory.exception.InventoryException;
import com.inventory.model.Supplier;
import com.inventory.service.SupplierService;
import com.inventory.util.DatabaseConnection;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SupplierServiceTest {

    private static SupplierService supplierService;
    private static int testSupplierId;

    @BeforeAll
    static void setup() {
        DatabaseConnection.initializeDatabase();
        supplierService = new SupplierService();
    }

    @Test
    @Order(1)
    void testAddSupplier() throws InventoryException {
        Supplier s = new Supplier();
        s.setName("Test Supplier Co.");
        s.setContactPerson("John Doe");
        s.setPhone("9876543210");
        s.setEmail("john@testsupplier.com");
        s.setAddress("123 Test Street, Mumbai");
        supplierService.addSupplier(s);
        assertTrue(s.getId() > 0, "Supplier ID should be assigned after add");
        testSupplierId = s.getId();
    }

    @Test
    @Order(2)
    void testGetAllSuppliers() {
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        assertFalse(suppliers.isEmpty());
    }

    @Test
    @Order(3)
    void testGetSupplierById() throws InventoryException {
        Supplier s = supplierService.getSupplierById(testSupplierId);
        assertNotNull(s);
        assertEquals("Test Supplier Co.", s.getName());
    }

    @Test
    @Order(4)
    void testUpdateSupplier() throws InventoryException {
        Supplier s = supplierService.getSupplierById(testSupplierId);
        s.setContactPerson("Jane Smith");
        supplierService.updateSupplier(s);
        Supplier updated = supplierService.getSupplierById(testSupplierId);
        assertEquals("Jane Smith", updated.getContactPerson());
    }

    @Test
    @Order(5)
    void testSearchSuppliers() {
        List<Supplier> results = supplierService.searchSuppliers("Test Supplier");
        assertFalse(results.isEmpty());
    }

    @Test
    @Order(6)
    void testDuplicateSupplierNameThrows() {
        Supplier dup = new Supplier();
        dup.setName("Test Supplier Co.");
        assertThrows(InventoryException.class, () -> supplierService.addSupplier(dup));
    }

    @Test
    @Order(7)
    void testDeleteSupplier() throws InventoryException {
        supplierService.deleteSupplier(testSupplierId);
        assertThrows(InventoryException.class, () ->
                supplierService.getSupplierById(testSupplierId)
        );
    }
}
