package com.example.bookstore.service;

import com.example.bookstore.dto.request.CategoryDTO;
import com.example.bookstore.dto.response.CategoryResponseDTO;
import com.example.bookstore.entity.Category;
import com.example.bookstore.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
        // Custom mapping for CategoryRequestDTO to Category (skip fields that shouldn't be updated)
        modelMapper.addMappings(new PropertyMap<CategoryDTO, Category>() {
            @Override
            protected void configure() {
                skip(destination.getId()); // Skip ID field during creation
                skip(destination.getCreatedAt()); // Skip audit fields
                skip(destination.getUpdatedAt());
                skip(destination.getIsDeleted());
            }
        });
    }

    @Override
    public CategoryResponseDTO createCategory(CategoryDTO request) {
        // Validate category name uniqueness
        if (categoryRepository.existsByName(request.getName())) {
            throw new RuntimeException("Category with name '" + request.getName() + "' already exists");
        }

        // Map DTO to entity
        Category category = modelMapper.map(request, Category.class);

        // Save category
        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryResponseDTO.class);
    }

    @Override
    public CategoryResponseDTO updateCategory(Integer id, CategoryDTO request) {
        // Find existing category
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + id));

        // Validate category name uniqueness (excluding current category)
        if (!category.getName().equals(request.getName()) && categoryRepository.existsByName(request.getName())) {
            throw new RuntimeException("Category with name '" + request.getName() + "' already exists");
        }

        // Map DTO to entity (update fields)
        modelMapper.map(request, category);

        // Save updated category
        Category updatedCategory = categoryRepository.save(category);
        return modelMapper.map(updatedCategory, CategoryResponseDTO.class);
    }

    @Override
    public CategoryResponseDTO getCategoryById(Integer id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found with ID: " + id));
        return modelMapper.map(category, CategoryResponseDTO.class);
    }

    @Override
    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(category -> modelMapper.map(category, CategoryResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCategory(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + id));
        categoryRepository.delete(category); // Soft delete due to @SQLDelete
    }

    @Override
    public Optional<Category> findCategoryById(Integer id) {
        return categoryRepository.findById(id);
    }


}
