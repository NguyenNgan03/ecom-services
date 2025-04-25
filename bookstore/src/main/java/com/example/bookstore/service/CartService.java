package com.example.bookstore.service;

import com.example.bookstore.dto.request.AddCartItemRequestDTO;
import com.example.bookstore.dto.request.UpdateCartItemRequestDTO;
import com.example.bookstore.dto.response.CartItemResponseDTO;
import com.example.bookstore.dto.response.CartResponseDTO;

public interface CartService {
    CartResponseDTO getCart();
    CartItemResponseDTO addItemToCart(AddCartItemRequestDTO request);
    CartItemResponseDTO updateItemQuantity(Integer cartItemId, UpdateCartItemRequestDTO request);
    void removeItemFromCart(Integer cartItemId);
    void clearCart();
}