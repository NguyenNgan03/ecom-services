package com.example.bookstore.dto.request;

import com.example.bookstore.entity.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class UpdateCartItemRequestDTO {
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @Getter
    @Setter
    public static class CreateOrderRequestDTO {
        @NotBlank(message = "Shipping address is required")
        private String shippingAddress;

        @NotNull(message = "Payment method is required")
        private PaymentMethod paymentMethod;
    }
}