package com.example.bookstore.service;

import com.example.bookstore.dto.request.UserDTO;
import com.example.bookstore.dto.response.RoleResponseDTO;
import com.example.bookstore.dto.response.UserResponseDTO;
import com.example.bookstore.entity.Role;
import com.example.bookstore.entity.User;
import com.example.bookstore.repository.UserRepository;
import com.example.bookstore.security.BookstoreUserDetails;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        modelMapper.addMappings(new PropertyMap<UserDTO, User>() {
            @Override
            protected void configure() {
                skip(destination.getId());
                skip(destination.getCreatedAt());
                skip(destination.getUpdatedAt());
                skip(destination.getIsDeleted());
                skip(destination.getIsActive());
                skip(destination.getRole());
                skip(destination.getReviews());
                skip(destination.getCart());
                skip(destination.getOrders());
            }
        });

        modelMapper.addMappings(new PropertyMap<User, UserResponseDTO>() {
            @Override
            protected void configure() {
                map(source.getRole().getName(), destination.getRoleName());
            }
        });
    }


    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        System.out.println("User role from DB: " + user.getRole().getName());
        return new BookstoreUserDetails(user);
    }

    @Override
    @Transactional
    public UserResponseDTO createUser(UserDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User with email '" + request.getEmail() + "' already exists");
        }

        RoleResponseDTO roleResponseDTO = roleService.getRoleById(request.getRoleId());
        Role role = new Role();
        role.setId(roleResponseDTO.getId());
        role.setName(roleResponseDTO.getName().toLowerCase());

        User user = modelMapper.map(request, User.class);
        user.setRole(role);
        user.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserResponseDTO.class);
    }

    @Override
    @Transactional
    public UserResponseDTO updateUser(Integer id, UserDTO request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User with email '" + request.getEmail() + "' already exists");
        }

        RoleResponseDTO roleResponseDTO = roleService.getRoleById(request.getRoleId());
        Role role = new Role();
        role.setId(roleResponseDTO.getId());
        role.setName(roleResponseDTO.getName());

        modelMapper.map(request, user);
        user.setRole(role);
        user.setIsActive(request.getIsActive() != null ? request.getIsActive() : user.getIsActive());

        User updatedUser = userRepository.save(user);
        return modelMapper.map(updatedUser, UserResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        return modelMapper.map(user, UserResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return modelMapper.map(user, UserResponseDTO.class);
    }
}