package com.example.bookstore.service;

import com.example.bookstore.security.BookstoreUserDetails;
import com.example.bookstore.security.JwtRequestFilter;
import com.example.bookstore.service.UserServiceImpl;
import com.example.bookstore.util.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Method;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class JwtRequestFilterTest {

    @Mock
    private UserServiceImpl userService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtRequestFilter jwtRequestFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Khởi tạo các mock
        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterInternal_ValidToken_SetsAuthentication() throws Exception {
        // Tạo key an toàn cho HS256 (256 bit)
        var key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);

        // Tạo token với key an toàn
        String token = Jwts.builder()
                .setSubject("admin4@gmail.com")
                .claim("role", "admin") // Đảm bảo role được thêm vào token
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000000))
                .signWith(key)
                .compact();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);

        BookstoreUserDetails userDetails = mock(BookstoreUserDetails.class);
        when(userService.loadUserByUsername("admin4@gmail.com")).thenReturn(userDetails);
        when(jwtUtil.extractUsername(token)).thenReturn("admin4@gmail.com");
        when(jwtUtil.validateToken(token, "admin4@gmail.com")).thenReturn(true);
        when(jwtUtil.extractClaim(anyString(), any())).thenReturn("admin"); // Đảm bảo mock trả về "admin"

        // Sử dụng Reflection để gọi doFilterInternal
        Method doFilterInternalMethod = JwtRequestFilter.class.getDeclaredMethod(
                "doFilterInternal", HttpServletRequest.class, HttpServletResponse.class, FilterChain.class);
        doFilterInternalMethod.setAccessible(true); // Bỏ qua kiểm tra access modifier

        doFilterInternalMethod.invoke(jwtRequestFilter, request, new MockHttpServletResponse(), filterChain);

        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("admin4@gmail.com", authentication.getPrincipal());
        assertEquals(1, authentication.getAuthorities().size());
        assertEquals("admin", authentication.getAuthorities().iterator().next().getAuthority());

        verify(filterChain).doFilter(request, any(HttpServletResponse.class));
    }

    @Test
    void testDoFilterInternal_InvalidToken_ReturnsUnauthorized() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalidtoken");

        when(jwtUtil.extractUsername("invalidtoken")).thenThrow(new RuntimeException("Invalid token"));

        MockHttpServletResponse response = new MockHttpServletResponse();

        // Sử dụng Reflection để gọi doFilterInternal
        Method doFilterInternalMethod = JwtRequestFilter.class.getDeclaredMethod(
                "doFilterInternal", HttpServletRequest.class, HttpServletResponse.class, FilterChain.class);
        doFilterInternalMethod.setAccessible(true); // Bỏ qua kiểm tra access modifier

        doFilterInternalMethod.invoke(jwtRequestFilter, request, response, filterChain);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        verify(filterChain, never()).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class));
    }
}