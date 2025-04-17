package com.example.bookstore.service;

import com.example.bookstore.dto.request.ProductDTO;
import com.example.bookstore.dto.response.ProductResponseDTO;

import java.util.List;

public interface ProductService {
    // Create a new product
    ProductResponseDTO createProduct(ProductDTO request);

    // Update an existing product
    ProductResponseDTO updateProduct(Integer id, ProductDTO request);

    // Get a product by ID
    ProductResponseDTO getProductById(Integer id);

    // Get all products
    List<ProductResponseDTO> getAllProducts();

    // Get all featured products
    List<ProductResponseDTO> getFeaturedProducts();

    // Get all products by category ID
    List<ProductResponseDTO> getProductsByCategory(Integer categoryId);

    // Delete a product (soft delete)
    void deleteProduct(Integer id);

}
