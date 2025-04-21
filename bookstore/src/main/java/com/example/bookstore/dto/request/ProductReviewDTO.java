package com.example.bookstore.dto.request;

import lombok.Data;

@Data
public class ProductReviewDTO {
    private Integer productId;
    private Integer rating;
    private String comment;
}
