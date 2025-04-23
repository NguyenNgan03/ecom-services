package com.example.bookstore.controller;

import com.example.bookstore.dto.request.LoginRequestDTO;
import com.example.bookstore.dto.request.UserDTO;
import com.example.bookstore.dto.response.LoginResponseDTO;
import com.example.bookstore.dto.response.UserResponseDTO;
import com.example.bookstore.service.UserService;
import com.example.bookstore.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication API", description = "API for user authentication and registration")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates a user and returns a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Integer userId = Integer.parseInt(userDetails.getUsername().split("@")[0]); // Adjust based on email format
        String role = userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

        String token = jwtUtil.generateToken(userId, role);
        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(token);
        response.setUserId(userId);
        response.setRole(role);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Registers a new user (public access)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "User with the same email already exists")
    })
    public ResponseEntity<UserResponseDTO> register(@RequestBody UserDTO request) {
        // Default to   role (roleId = 2, assuming 1 is ADMIN)
        request.setRoleId(2);
        UserResponseDTO response = userService.createUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logs out the authenticated user (client should remove token)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Void> logout() {
        // In a stateless JWT system, logout is handled by the client removing the token
        // Clear the security context (optional, since we're stateless)
        SecurityContextHolder.clearContext();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
