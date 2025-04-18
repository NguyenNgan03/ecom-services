package com.example.bookstore.service;

import com.example.bookstore.dto.request.UserDTO;
import com.example.bookstore.dto.response.RoleResponseDTO;
import com.example.bookstore.dto.response.UserResponseDTO;
import com.example.bookstore.entity.Role;
import com.example.bookstore.entity.User;
import com.example.bookstore.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final ModelMapper modelMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleService roleService, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.modelMapper = modelMapper;

        // Custom mapping for UserRequestDTO to User
        modelMapper.addMappings(new PropertyMap<UserDTO, User>() {
            @Override
            protected void configure() {
                skip(destination.getId()); // Skip ID field during creation
                skip(destination.getCreatedAt()); // Skip audit fields
                skip(destination.getUpdatedAt());
                skip(destination.getIsDeleted());
                skip(destination.getIsActive()); // Skip isActive, we'll set it manually
                skip(destination.getRole()); // Skip role, we'll set it manually
                skip(destination.getReviews());
                skip(destination.getCart());
                skip(destination.getOrders());
            }
        });

        // Custom mapping for User to UserResponseDTO (role.getName() to roleName)
        modelMapper.addMappings(new PropertyMap<User, UserResponseDTO>() {
            @Override
            protected void configure() {
                map(source.getRole().getName(), destination.getRoleName());
            }
        });
    }

    @Override
    public UserResponseDTO createUser(UserDTO request) {
        // Validate email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User with email '" + request.getEmail() + "' already exists");
        }

        // Fetch and validate role
        RoleResponseDTO roleResponseDTO = roleService.getRoleById(request.getRoleId());
        Role role = new Role();
        role.setId(roleResponseDTO.getId());
        role.setName(roleResponseDTO.getName());

        // Map DTO to entity
        User user = modelMapper.map(request, User.class);
        user.setRole(role);
        // TODO: Hash the password using BCryptPasswordEncoder before saving (after implementing authentication)
        user.setPassword(request.getPassword()); // Plain text for now
        user.setIsActive(request.getIsActive() != null ? request.getIsActive() : true); // Default to true if not specified

        // Save user
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserResponseDTO.class);
    }

    @Override
    public UserResponseDTO updateUser(Integer id, UserDTO request) {
        // Find existing user
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        // Validate email uniqueness (excluding current user)
        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User with email '" + request.getEmail() + "' already exists");
        }

        // Fetch and validate role
        RoleResponseDTO roleResponseDTO = roleService.getRoleById(request.getRoleId());
        Role role = new Role();
        role.setId(roleResponseDTO.getId());
        role.setName(roleResponseDTO.getName());

        // Map DTO to entity (update fields)
        modelMapper.map(request, user);
        user.setRole(role);
        // TODO: Hash the password using BCryptPasswordEncoder if updated (after implementing authentication)
        user.setPassword(request.getPassword()); // Plain text for now
        user.setIsActive(request.getIsActive() != null ? request.getIsActive() : user.getIsActive()); // Retain existing value if not specified

        // Save updated user
        User updatedUser = userRepository.save(user);
        return modelMapper.map(updatedUser, UserResponseDTO.class);
    }

    @Override
    public UserResponseDTO getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        return modelMapper.map(user, UserResponseDTO.class);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        userRepository.delete(user); // Soft delete due to @SQLDelete
    }
}