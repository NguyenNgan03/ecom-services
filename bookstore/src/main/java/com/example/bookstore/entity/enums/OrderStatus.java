package com.example.bookstore.entity.enums;

public enum OrderStatus {
    PENDING,       // Đơn hàng mới tạo
    PROCESSING,    // Đang xử lý
    SHIPPED,       // Đã giao cho đơn vị vận chuyển
    DELIVERED,     // Giao hàng thành công
    CANCELLED
}
