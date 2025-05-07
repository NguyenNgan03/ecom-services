package com.example.bookstore.service;

import com.example.bookstore.dto.request.UserDTO;
import com.example.bookstore.dto.response.UserResponseDTO;

import java.util.List;

public interface UserService {
    // Create a new user
    UserResponseDTO createUser(UserDTO request);

    // Update an existing user
    UserResponseDTO updateUser(Integer id, UserDTO request);

    // Get a user by ID
    UserResponseDTO getUserById(Integer id);

    // Get all users
    List<UserResponseDTO> getAllUsers();

    // Delete a user (soft delete)
    void deleteUser(Integer id);

    // Get profile of the authenticated user
    UserResponseDTO getUserProfile(String email);
}
