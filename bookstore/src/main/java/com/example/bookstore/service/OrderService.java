package com.example.bookstore.service;

import com.example.bookstore.dto.request.CreateOrderRequestDTO;
import com.example.bookstore.dto.response.OrderResponseDTO;

import java.util.List;

public interface OrderService {
    OrderResponseDTO createOrder(CreateOrderRequestDTO request);
    List<OrderResponseDTO> getOrders();
    OrderResponseDTO getOrder(Integer orderId);
}