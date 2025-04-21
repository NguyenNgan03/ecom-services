package com.example.bookstore.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductReviewResponseDTO {
    private Integer id;
    private Integer productId;
    private String productName;
    private Integer userId;
    private String userName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
