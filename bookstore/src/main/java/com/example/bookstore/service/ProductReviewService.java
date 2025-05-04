package com.example.bookstore.service;

import com.example.bookstore.dto.request.ProductReviewDTO;
import com.example.bookstore.dto.response.ProductReviewResponseDTO;

import java.util.List;

public interface ProductReviewService {
    // Create a new review using email from token
    ProductReviewResponseDTO createReview(String email, ProductReviewDTO request);

    // Update an existing review
    ProductReviewResponseDTO updateReview(Integer id, Integer userId, ProductReviewDTO request);

    // Get a review by ID
    ProductReviewResponseDTO getReviewById(Integer id);

    // Get all reviews for a product
    List<ProductReviewResponseDTO> getReviewsByProductId(Integer productId);

    // Delete a review
    void deleteReview(Integer id, Integer userId);
}