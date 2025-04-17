package com.example.bookstore.service;

import com.example.bookstore.dto.request.RoleDTO;
import com.example.bookstore.dto.response.RoleResponseDTO;
import com.example.bookstore.entity.Role;
import com.example.bookstore.repository.RoleRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository, ModelMapper modelMapper) {
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;

        // Custom mapping for RoleRequestDTO to Role (skip fields that shouldn't be updated)
        modelMapper.addMappings(new PropertyMap<RoleDTO, Role>() {
            @Override
            protected void configure() {
                skip(destination.getId()); // Skip ID field during creation
                skip(destination.getCreatedAt()); // Skip audit fields
                skip(destination.getUpdatedAt());
                skip(destination.getIsDeleted());
                skip(destination.getUsers());
            }
        });
    }

    @Override
    public RoleResponseDTO createRole(RoleDTO request) {
        // Validate role name uniqueness
        if (roleRepository.existsByName(request.getName())) {
            throw new RuntimeException("Role with name '" + request.getName() + "' already exists");
        }

        // Map DTO to entity
        Role role = modelMapper.map(request, Role.class);

        // Save role
        Role savedRole = roleRepository.save(role);
        return modelMapper.map(savedRole, RoleResponseDTO.class);
    }

    @Override
    public RoleResponseDTO updateRole(Integer id, RoleDTO request) {
        // Find existing role
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + id));

        // Validate role name uniqueness (excluding current role)
        if (!role.getName().equals(request.getName()) && roleRepository.existsByName(request.getName())) {
            throw new RuntimeException("Role with name '" + request.getName() + "' already exists");
        }

        // Map DTO to entity (update fields)
        modelMapper.map(request, role);

        // Save updated role
        Role updatedRole = roleRepository.save(role);
        return modelMapper.map(updatedRole, RoleResponseDTO.class);
    }

    @Override
    public RoleResponseDTO getRoleById(Integer id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + id));
        return modelMapper.map(role, RoleResponseDTO.class);
    }

    @Override
    public List<RoleResponseDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(role -> modelMapper.map(role, RoleResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteRole(Integer id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + id));
        roleRepository.delete(role); // Soft delete due to @SQLDelete
    }
}