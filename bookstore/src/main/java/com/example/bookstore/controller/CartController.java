package com.example.bookstore.controller;

import com.example.bookstore.dto.request.AddCartItemRequestDTO;
import com.example.bookstore.dto.request.UpdateCartItemRequestDTO;
import com.example.bookstore.dto.response.CartItemResponseDTO;
import com.example.bookstore.dto.response.CartResponseDTO;
import com.example.bookstore.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
@Tag(name = "Cart APIs", description = "APIs for managing user's shopping cart")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @Operation(summary = "Get user's cart", description = "Retrieve the shopping cart of the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the cart"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User must be logged in"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<CartResponseDTO> getCart() {
        return ResponseEntity.ok(cartService.getCart());
    }

    @Operation(summary = "Add item to cart", description = "Add a product to the authenticated user's cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully added item to cart"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or validation failed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User must be logged in"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/items")
    public ResponseEntity<CartItemResponseDTO> addItemToCart(
            @Parameter(description = "Request body containing product ID and quantity to add to cart", required = true)
            @Valid @RequestBody AddCartItemRequestDTO request) {
        return ResponseEntity.ok(cartService.addItemToCart(request));
    }

    @Operation(summary = "Update cart item quantity", description = "Update the quantity of a specific item in the authenticated user's cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated cart item quantity"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or validation failed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User must be logged in"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not own this cart item"),
            @ApiResponse(responseCode = "404", description = "Cart item not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/items/{id}")
    public ResponseEntity<CartItemResponseDTO> updateItemQuantity(
            @Parameter(description = "ID of the cart item to update", required = true)
            @PathVariable Integer id,
            @Parameter(description = "Request body containing the new quantity", required = true)
            @Valid @RequestBody UpdateCartItemRequestDTO request) {
        return ResponseEntity.ok(cartService.updateItemQuantity(id, request));
    }

    @Operation(summary = "Remove item from cart", description = "Remove a specific item from the authenticated user's cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully removed item from cart"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User must be logged in"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not own this cart item"),
            @ApiResponse(responseCode = "404", description = "Cart item not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> removeItemFromCart(
            @Parameter(description = "ID of the cart item to remove", required = true)
            @PathVariable Integer id) {
        cartService.removeItemFromCart(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Clear cart", description = "Remove all items from the authenticated user's cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully cleared the cart"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User must be logged in"),
            @ApiResponse(responseCode = "404", description = "Cart not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart();
        return ResponseEntity.noContent().build();
    }
}