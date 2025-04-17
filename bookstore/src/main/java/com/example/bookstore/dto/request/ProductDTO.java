package com.example.bookstore.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDTO {
    @NotBlank(message = "Product name is required")
    @Size(max = 255, message = "Product name must be less than 255 characters")
    private String name;

    @NotBlank(message = "Image URL is required")
    @Size(max = 512, message = "Image URL must be less than 512 characters")
    private String imageUrl;

    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    @NotBlank(message = "Author is required")
    @Size(max = 100, message = "Author name must be less than 100 characters")
    private String author;

    @NotNull(message = "Category ID is required")
    @Positive(message = "Category ID must be positive")
    private Integer categoryId;

    private Boolean isFeatured = false;

    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock = 0;
}
