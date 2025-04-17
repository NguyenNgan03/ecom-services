package com.example.bookstore.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductResponseDTO {
    private Integer id;
    private String name;
    private String imageUrl;
    private String description;
    private BigDecimal price;
    private String author;
    private boolean isFeatured;
    private int stock;
    private BigDecimal averageRating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer categoryId;
    private String categoryName;
    private Boolean isDeleted;
}
