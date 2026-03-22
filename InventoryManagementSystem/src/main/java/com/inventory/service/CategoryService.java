package com.inventory.service;

import com.inventory.dao.CategoryDAO;
import com.inventory.dao.CategoryDAOImpl;
import com.inventory.exception.InventoryException;
import com.inventory.model.Category;
import com.inventory.util.ValidationUtil;

import java.util.List;

public class CategoryService {

    private final CategoryDAO categoryDAO;

    public CategoryService() {
        this.categoryDAO = new CategoryDAOImpl();
    }

    public void addCategory(Category category) throws InventoryException {
        ValidationUtil.requireNonEmpty(category.getName(), "Category name");
        if (categoryDAO.getCategoryByName(category.getName()).isPresent()) {
            throw new InventoryException("Category '" + category.getName() + "' already exists.");
        }
        categoryDAO.addCategory(category);
    }

    public void updateCategory(Category category) throws InventoryException {
        ValidationUtil.requireNonEmpty(category.getName(), "Category name");
        getCategoryOrThrow(category.getId());
        categoryDAO.updateCategory(category);
    }

    public void deleteCategory(int id) throws InventoryException {
        getCategoryOrThrow(id);
        categoryDAO.deleteCategory(id);
    }

    public List<Category> getAllCategories() {
        return categoryDAO.getAllCategories();
    }

    private Category getCategoryOrThrow(int id) throws InventoryException {
        return categoryDAO.getCategoryById(id)
                .orElseThrow(() -> new InventoryException("Category with ID " + id + " not found."));
    }
}
