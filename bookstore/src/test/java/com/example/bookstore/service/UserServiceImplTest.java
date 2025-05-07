package com.example.bookstore.service;

import com.example.bookstore.dto.request.UserDTO;
import com.example.bookstore.dto.response.RoleResponseDTO;
import com.example.bookstore.dto.response.UserResponseDTO;
import com.example.bookstore.entity.Role;
import com.example.bookstore.entity.User;
import com.example.bookstore.repository.UserRepository;
import com.example.bookstore.security.BookstoreUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleService roleService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDTO userDTO;
    private Role role;

    @BeforeEach
    void setUp() {
        // Khởi tạo các mock
        org.mockito.MockitoAnnotations.openMocks(this);

        role = new Role();
        role.setId(1);
        role.setName("admin");

        user = new User();
        user.setId(1);
        user.setEmail("admin4@gmail.com");
        user.setPassword("password");
        user.setRole(role);
        user.setIsActive(true);
        user.setIsDeleted(false);

        userDTO = new UserDTO();
        userDTO.setEmail("admin4@gmail.com");
        userDTO.setPassword("password");
        userDTO.setRoleId(1);
        userDTO.setIsActive(true);
    }

    @Test
    void testLoadUserByUsername_ValidEmail_ReturnsUserDetails() {
        when(userRepository.findByEmail("admin4@gmail.com")).thenReturn(Optional.of(user));

        BookstoreUserDetails userDetails = (BookstoreUserDetails) userService.loadUserByUsername("admin4@gmail.com");
        assertEquals("admin4@gmail.com", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertEquals(1, userDetails.getAuthorities().size());
        assertEquals("admin", userDetails.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void testLoadUserByUsername_InvalidEmail_ThrowsException() {
        when(userRepository.findByEmail("unknown@gmail.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                userService.loadUserByUsername("unknown@gmail.com"));
    }

    @Test
    void testCreateUser_ValidUser_ReturnsUserResponseDTO() {
        RoleResponseDTO roleResponseDTO = new RoleResponseDTO();
        roleResponseDTO.setId(1);
        roleResponseDTO.setName("admin");

        when(roleService.getRoleById(1)).thenReturn(roleResponseDTO);
        when(modelMapper.map(userDTO, User.class)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(modelMapper.map(user, UserResponseDTO.class)).thenReturn(new UserResponseDTO());

        UserResponseDTO response = userService.createUser(userDTO);
        assertEquals(1, response.getId()); // Giả định ID được ánh xạ
        verify(userRepository).save(user);
    }
}