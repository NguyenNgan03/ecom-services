package com.example.bookstore.security;

import com.example.bookstore.service.UserServiceImpl;
import com.example.bookstore.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        log.info("Processing request for URI: {}", requestURI);

        if (isPublicEndpoint(requestURI, request.getMethod())) {
            log.info("Public endpoint, skipping authentication: {}", requestURI);
            chain.doFilter(request, response);
            return;
        }

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
                log.info("Extracted username from token: {}", username);
            } catch (ExpiredJwtException e) {
                log.error("JWT token expired: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"JWT token is expired\"}");
                return;
            } catch (Exception e) {
                log.error("Invalid JWT token: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Invalid JWT token\"}");
                return;
            }
        } else {
            log.warn("Authorization header missing or invalid for URI: {}", requestURI);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userService.loadUserByUsername(username);
            if (jwtUtil.validateToken(jwt, username)) {
                String role = jwtUtil.extractClaim(jwt, claims -> claims.get("role", String.class));
                log.info("Extracted role from token: {}", role);
                var authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
                log.info("Authorities set for user: {}", authorities);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("Authentication set for user: {}", username);
            } else {
                log.warn("Token validation failed for username: {}", username);
            }
        } else if (username == null) {
            log.warn("No username extracted from token for URI: {}", requestURI);
        }
        chain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String requestURI, String method) {
        // Chỉ cho phép GET /api/products/{id} là public, các phương thức khác yêu cầu xác thực
        if (requestURI.matches("/api/products/\\d+") && "GET".equalsIgnoreCase(method)) {
            return true;
        }
        return requestURI.startsWith("/api/auth") ||
                requestURI.startsWith("/api/categories") ||
                requestURI.startsWith("/api/products/featured") ||
                requestURI.startsWith("/api/products/category/") ||
                requestURI.matches("/api/products/\\d+/details") ||
                requestURI.startsWith("/swagger-ui") ||
                requestURI.startsWith("/api/auth/register") ||
                requestURI.startsWith("/v3/api-docs") ||
                requestURI.startsWith("/api/orders/") ||
                requestURI.startsWith("/api/cart/") ||
                requestURI.startsWith("/api/roles/");
    }
}