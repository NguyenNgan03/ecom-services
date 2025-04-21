package com.example.bookstore.repository;

import com.example.bookstore.entity.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Integer> {

    // Find all reviews for a product
    List<ProductReview> findByProductId(Integer productId);

    // Check if a user has already reviewed a product
    boolean existsByProductIdAndUserId(Integer productId, Integer userId);
}
