package com.example.bookstore.service;

import com.example.bookstore.dto.request.CategoryDTO;
import com.example.bookstore.dto.response.CategoryResponseDTO;
import com.example.bookstore.model.Category;
import com.example.bookstore.repository.CategoryRepository;

import java.util.List;

public interface CategoryService {
    CategoryDTO createCategory(CategoryDTO categoryDTO);

    List<CategoryResponseDTO> getAllCategories();

    CategoryResponseDTO getCategoryById(Integer id);

    CategoryDTO updateCategory(Integer id, CategoryDTO categoryDTO);

    void deleteCategory(Integer id);

//    Category findByName(String name);
}