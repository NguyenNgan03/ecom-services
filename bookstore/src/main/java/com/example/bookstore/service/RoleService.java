package com.example.bookstore.service;

import com.example.bookstore.dto.request.RoleDTO;
import com.example.bookstore.dto.response.RoleResponseDTO;

import java.util.List;

public interface RoleService {
    // Create a new role
    RoleResponseDTO createRole(RoleDTO request);

    // Update an existing role
    RoleResponseDTO updateRole(Integer id, RoleDTO request);

    // Get a role by ID
    RoleResponseDTO getRoleById(Integer id);

    // Get all roles
    List<RoleResponseDTO> getAllRoles();

    // Delete a role (soft delete)
    void deleteRole(Integer id);
}
