package com.example.bookstore.service;

import com.example.bookstore.dto.request.CategoryDTO;
import com.example.bookstore.dto.response.CategoryResponseDTO;
import com.example.bookstore.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    // Create a new category
    CategoryResponseDTO createCategory(CategoryDTO request);

    // Update an existing category
    CategoryResponseDTO updateCategory(Integer id, CategoryDTO request);

    // Get a category by ID
    CategoryResponseDTO getCategoryById(Integer id);

    // Get all categories
    List<CategoryResponseDTO> getAllCategories();

    // Delete a category (soft delete)
    void deleteCategory(Integer id);

    // Find category entity by ID (used internally by other services like ProductService)
    Optional<Category> findCategoryById(Integer id);
}