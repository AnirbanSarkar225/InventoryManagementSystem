package com.inventory.dao;

import com.inventory.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryDAO {
    void addCategory(Category category);
    void updateCategory(Category category);
    void deleteCategory(int id);
    Optional<Category> getCategoryById(int id);
    Optional<Category> getCategoryByName(String name);
    List<Category> getAllCategories();
}
