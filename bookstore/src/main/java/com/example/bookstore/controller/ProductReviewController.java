package com.example.bookstore.controller;

import com.example.bookstore.dto.request.ProductDTO;
import com.example.bookstore.dto.request.ProductReviewDTO;
import com.example.bookstore.dto.response.ProductResponseDTO;
import com.example.bookstore.dto.response.ProductReviewResponseDTO;
import com.example.bookstore.service.ProductReviewService;
import com.example.bookstore.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Product Review API", description = "API for managing product reviews in the bookstore")
public class ProductReviewController {

    private final ProductReviewService productReviewService;

    @Autowired
    public ProductReviewController(ProductReviewService productReviewService) {
        this.productReviewService = productReviewService;
    }

    @PostMapping
    @Operation(summary = "Create a product review", description = "Creates a new review for a product (authenticated user)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Review created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "User or product not found"),
            @ApiResponse(responseCode = "409", description = "User has already reviewed this product")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductReviewResponseDTO> createReview(
            @RequestBody ProductReviewDTO
                    request,
            @RequestParam Integer userId) {
        ProductReviewResponseDTO response = productReviewService.createReview(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product review", description = "Updates an existing review by ID (authenticated user)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "User not authorized to update this review"),
            @ApiResponse(responseCode = "404", description = "Review or product not found")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductReviewResponseDTO> updateReview(
            @PathVariable Integer id,
            @RequestBody ProductReviewDTO request,
            @RequestParam Integer userId) {
        ProductReviewResponseDTO response = productReviewService.updateReview(id, userId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a review by ID", description = "Retrieves a review by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    public ResponseEntity<ProductReviewResponseDTO> getReviewById(@PathVariable Integer id) {
        ProductReviewResponseDTO response = productReviewService.getReviewById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get all reviews for a product", description = "Retrieves all reviews for a specific product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reviews retrieved successfully")
    })
    public ResponseEntity<List<ProductReviewResponseDTO>> getReviewsByProductId(@PathVariable Integer productId) {
        List<ProductReviewResponseDTO> response = productReviewService.getReviewsByProductId(productId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a review", description = "Deletes a review by ID (authenticated user)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Review deleted successfully"),
            @ApiResponse(responseCode = "403", description = "User not authorized to delete this review"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Integer id,
            @RequestParam Integer userId) {
        productReviewService.deleteReview(id, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}