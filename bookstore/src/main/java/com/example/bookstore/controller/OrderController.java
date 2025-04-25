package com.example.bookstore.controller;

import com.example.bookstore.dto.request.CreateOrderRequestDTO;
import com.example.bookstore.dto.response.OrderResponseDTO;
import com.example.bookstore.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order APIs", description = "APIs for managing user's orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Create an order", description = "Create a new order from the authenticated user's cart")
    @

            ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created the order"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or validation failed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User must be logged in"),
            @ApiResponse(responseCode = "404", description = "Cart is empty"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(
            @Parameter(description = "Request body containing shipping address and payment method", required = true)
            @Valid @RequestBody CreateOrderRequestDTO request) {
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    @Operation(summary = "Get user's orders", description = "Retrieve the list of orders for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of orders"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User must be logged in"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getOrders() {
        return ResponseEntity.ok(orderService.getOrders());
    }

    @Operation(summary = "Get order details", description = "Retrieve the details of a specific order by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the order details"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User must be logged in"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not own this order"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrder(
            @Parameter(description = "ID of the order to retrieve", required = true)
            @PathVariable Integer id) {
        return ResponseEntity.ok(orderService.getOrder(id));
    }
}