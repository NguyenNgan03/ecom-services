package com.example.bookstore.controller;

import com.example.bookstore.dto.request.ProductDTO;
import com.example.bookstore.dto.response.ProductResponseDTO;
import com.example.bookstore.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/products")
@Tag(name = "Product API", description = "API for managing products in the bookstore")
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @Operation(summary = "Create a new product", description = "Creates a new product in the bookstore (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "Product with the same name already exists")
    })
    // TODO: Add @PreAuthorize("hasRole('ADMIN')") after implementing authentication
    public ResponseEntity<ProductResponseDTO> createProduct(@RequestBody ProductDTO request) {
        ProductResponseDTO response = productService.createProduct(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product", description = "Updates an existing product by ID (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "409", description = "Product with the same name already exists")
    })
    // TODO: Add @PreAuthorize("hasRole('ADMIN')") after implementing authentication
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Integer id,
            @RequestBody ProductDTO request) {
        ProductResponseDTO response = productService.updateProduct(id, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a product by ID", description = "Retrieves a product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Integer id) {
        ProductResponseDTO response = productService.getProductById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieves all products (primarily for admin use)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully")
    })
    // TODO: Add @PreAuthorize("hasRole('ADMIN')") after implementing authentication
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<ProductResponseDTO> response = productService.getAllProducts();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/featured")
    @Operation(summary = "Get featured products", description = "Retrieves all featured products for the home page")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Featured products retrieved successfully")
    })
    public ResponseEntity<List<ProductResponseDTO>> getFeaturedProducts() {
        List<ProductResponseDTO> response = productService.getFeaturedProducts();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get products by category", description = "Retrieves all products in a specific category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<List<ProductResponseDTO>> getProductsByCategory(@PathVariable Integer categoryId) {
        List<ProductResponseDTO> response = productService.getProductsByCategory(categoryId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product", description = "Deletes a product by ID (soft delete, Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    // TODO: Add @PreAuthorize("hasRole('ADMIN')") after implementing authentication
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
