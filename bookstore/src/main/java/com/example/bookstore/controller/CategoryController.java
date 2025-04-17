package com.example.bookstore.controller;

import com.example.bookstore.dto.request.CategoryDTO;
import com.example.bookstore.dto.response.CategoryResponseDTO;
import com.example.bookstore.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Category Management", description = "APIs for managing product categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {

        this.categoryService = categoryService;
    }

    //create
    @PostMapping
    @Operation(summary = "Create a new category", description = "Creates a new category in the bookstore (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "Category with the same name already exists")
    })
    // TODO: Add @PreAuthorize("hasRole('ADMIN')") after implementing authentication
    public ResponseEntity<CategoryResponseDTO> createCategory(@RequestBody CategoryDTO request) {
        CategoryResponseDTO response = categoryService.createCategory(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    //update
    @PutMapping("/{id}")
    @Operation(summary = "Update a category", description = "Updates an existing category by ID (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "409", description = "Category with the same name already exists")
    })
    // TODO: Add @PreAuthorize("hasRole('ADMIN')") after implementing authentication
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @PathVariable Integer id,
            @RequestBody CategoryDTO request) {
        CategoryResponseDTO response = categoryService.updateCategory(id, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //get by id
    @GetMapping("/{id}")
    @Operation(summary = "Get a category by ID", description = "Retrieves a category by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable Integer id) {
        CategoryResponseDTO response = categoryService.getCategoryById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // get all
    @GetMapping
    @Operation(summary = "Get all categories", description = "Retrieves all categories for the category menu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    })
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        List<CategoryResponseDTO> response = categoryService.getAllCategories();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //delete
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category", description = "Deletes a category by ID (soft delete, Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    // TODO: Add @PreAuthorize("hasRole('ADMIN')") after implementing authentication
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
