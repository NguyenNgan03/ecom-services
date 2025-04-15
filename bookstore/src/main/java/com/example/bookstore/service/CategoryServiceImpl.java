package com.example.bookstore.service;

import com.example.bookstore.dto.request.CategoryDTO;
import com.example.bookstore.dto.response.CategoryResponseDTO;
import com.example.bookstore.model.Category;
import com.example.bookstore.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

   @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
       if (categoryRepository.existsByName(categoryDTO.getName())) {
           throw new IllegalArgumentException("Category with name '" + categoryDTO.getName() + "' already exists");
       }
       Category category = modelMapper.map(categoryDTO, Category.class);
       Category savedCategory = categoryRepository.save(category);
       return modelMapper.map(savedCategory, CategoryDTO.class);
   }

   @Override
   public List<CategoryResponseDTO> getAllCategories() {
       return categoryRepository.findAll()
               .stream()
               .map(category -> modelMapper.map(category, CategoryResponseDTO.class))
               .collect(Collectors.toList());
   }

    @Override
    public CategoryResponseDTO getCategoryById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));
        return modelMapper.map(category, CategoryResponseDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(Integer id, CategoryDTO categoryDTO) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));
        if (!existingCategory.getName().equals(categoryDTO.getName()) &&
                categoryRepository.existsByName(categoryDTO.getName())) {
            throw new IllegalArgumentException("Category with name '" + categoryDTO.getName() + "' already exists");
        }
        existingCategory.setName(categoryDTO.getName());
        Category updatedCategory = categoryRepository.save(existingCategory);
        return modelMapper.map(updatedCategory, CategoryDTO.class);
    }

    @Override
    public void deleteCategory(Integer id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
