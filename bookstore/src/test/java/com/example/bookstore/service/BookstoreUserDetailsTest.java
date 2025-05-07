package com.example.bookstore.service;

import com.example.bookstore.entity.Role;
import com.example.bookstore.entity.User;
import com.example.bookstore.security.BookstoreUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BookstoreUserDetailsTest {

    private User user;
    private BookstoreUserDetails userDetails;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setId(1);
        role.setName("admin");

        user = new User();
        user.setEmail("admin4@gmail.com");
        user.setPassword("password");
        user.setRole(role);
        user.setIsActive(true);
        user.setIsDeleted(false);

        userDetails = new BookstoreUserDetails(user);
    }

    @Test
    void testGetAuthorities_ReturnsCorrectRole() {
        assertEquals(1, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("admin")));
    }

    @Test
    void testGetPassword_ReturnsUserPassword() {
        assertEquals("password", userDetails.getPassword());
    }

    @Test
    void testGetUsername_ReturnsUserEmail() {
        assertEquals("admin4@gmail.com", userDetails.getUsername());
    }

    @Test
    void testIsAccountNonExpired_ReturnsTrue() {
        assertTrue(userDetails.isAccountNonExpired());
    }

    @Test
    void testIsAccountNonLocked_ReturnsTrueWhenActive() {
        assertTrue(userDetails.isAccountNonLocked());
    }

    @Test
    void testIsCredentialsNonExpired_ReturnsTrue() {
        assertTrue(userDetails.isCredentialsNonExpired());
    }

    @Test
    void testIsEnabled_ReturnsTrueWhenNotDeleted() {
        assertTrue(userDetails.isEnabled());
    }
}