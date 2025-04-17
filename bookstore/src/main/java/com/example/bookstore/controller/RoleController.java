package com.example.bookstore.controller;

import com.example.bookstore.dto.request.RoleDTO;
import com.example.bookstore.dto.response.RoleResponseDTO;
import com.example.bookstore.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@Tag(name = "Role API", description = "API for managing roles in the bookstore")
public class RoleController {

    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    @Operation(summary = "Create a new role", description = "Creates a new role in the bookstore (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Role created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "Role with the same name already exists")
    })
    // TODO: Add @PreAuthorize("hasRole('ADMIN')") after implementing authentication
    public ResponseEntity<RoleResponseDTO> createRole(@RequestBody RoleDTO request) {
        RoleResponseDTO response = roleService.createRole(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a role", description = "Updates an existing role by ID (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Role not found"),
            @ApiResponse(responseCode = "409", description = "Role with the same name already exists")
    })
    // TODO: Add @PreAuthorize("hasRole('ADMIN')") after implementing authentication
    public ResponseEntity<RoleResponseDTO> updateRole(
            @PathVariable Integer id,
            @RequestBody RoleDTO request) {
        RoleResponseDTO response = roleService.updateRole(id, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a role by ID", description = "Retrieves a role by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Role not found")
    })
    public ResponseEntity<RoleResponseDTO> getRoleById(@PathVariable Integer id) {
        RoleResponseDTO response = roleService.getRoleById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get all roles", description = "Retrieves all roles for role management or selection")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Roles retrieved successfully")
    })
    public ResponseEntity<List<RoleResponseDTO>> getAllRoles() {
        List<RoleResponseDTO> response = roleService.getAllRoles();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a role", description = "Deletes a role by ID (soft delete, Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Role deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Role not found")
    })
    // TODO: Add @PreAuthorize("hasRole('ADMIN')") after implementing authentication
    public ResponseEntity<Void> deleteRole(@PathVariable Integer id) {
        roleService.deleteRole(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}