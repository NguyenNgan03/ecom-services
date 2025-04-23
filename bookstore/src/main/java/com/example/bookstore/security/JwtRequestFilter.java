package com.example.bookstore.security;

import com.example.bookstore.util.JwtUtil;
import jakarta.servlet.ServletException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final BookstoreUserDetailsService userDetailsService;

    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/swagger-ui/.*",
            "/v3/api-docs/.*",
            "/api-docs",
            "/api-docs/.*",
            "/api/auth/.*",       // Authentication API
            "/api/categories/.*", // Category Management API
            "/api/products/.*",   // Product API
            "/api/reviews/.*",    // Product Review API
            "/api/roles/.*",      // Role API
            "/api/users/.*"       // User API
    );

    @Autowired
    public JwtRequestFilter(JwtUtil jwtUtil, BookstoreUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String path = request.getRequestURI();

        // Skip filter for public endpoints
        boolean isPublicPath = PUBLIC_PATHS.stream().anyMatch(pattern -> path.matches(pattern));
        if (isPublicPath) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // Check if token is present
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Missing or invalid Authorization header");
            return;
        }

        token = authHeader.substring(7);

        // Validate token
        if (jwtUtil.validateToken(token)) {
            username = jwtUtil.getUserId(token).toString();
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Invalid or expired token");
            return;
        }

        // Set authentication if token is vaalid
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username + "@example.com");
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        chain.doFilter(request, response);
    }
}