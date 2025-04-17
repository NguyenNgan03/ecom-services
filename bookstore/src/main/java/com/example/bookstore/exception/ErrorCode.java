package com.example.bookstore.exception;

public enum ErrorCode {
    // Product errors
    PRODUCT_NOT_FOUND("PRODUCT-001", "Product not found"),
    PRODUCT_NAME_EXISTED("PRODUCT-002", "Product name already exists"),
    PRODUCT_OUT_OF_STOCK("PRODUCT-003", "Product is out of stock"),

    // Category errors
    CATEGORY_NOT_FOUND("CATEGORY-001", "Category not found"),
    CATEGORY_NAME_EXISTED("CATEGORY-002", "Category name already exists"),

    // Common errors
    INVALID_INPUT("COMMON-001", "Invalid input data"),
    INTERNAL_SERVER_ERROR("COMMON-002", "Internal server error");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
