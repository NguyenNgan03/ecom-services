package com.example.bookstore.controller;

import com.example.bookstore.dto.*;
import com.example.bookstore.dto.JwtResponseDTO;
import com.example.bookstore.dto.LoginRequestDTO;
import com.example.bookstore.dto.RefreshTokenRequestDTO;
import com.example.bookstore.dto.RefreshTokenResponseDTO;
import com.example.bookstore.dto.RegisterRequestDTO;
import com.example.bookstore.dto.request.UserDTO;
import com.example.bookstore.dto.response.UserResponseDTO;
import com.example.bookstore.entity.RefreshToken;
import com.example.bookstore.entity.User;
import com.example.bookstore.security.BookstoreUserDetails;
import com.example.bookstore.service.RefreshTokenService;
import com.example.bookstore.service.UserService;
import com.example.bookstore.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Operation(summary = "User login", description = "Authenticate user and return JWT and refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        String email = loginRequest.getEmail();
        User user = ((BookstoreUserDetails) authentication.getPrincipal()).getUser();
        String role = user.getRole().getName();

        String jwt = jwtUtil.generateToken(email, role);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(email);

        return ResponseEntity.ok(new JwtResponseDTO(jwt, refreshToken.getToken(), email, role));
    }

    @Operation(summary = "User registration", description = "Register a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registration successful"),
            @ApiResponse(responseCode = "400", description = "Email already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO registerRequest) {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(registerRequest.getEmail());
        userDTO.setPassword(registerRequest.getPassword());
        userDTO.setFirstName(registerRequest.getFirstName());
        userDTO.setLastName(registerRequest.getLastName());
        userDTO.setPhoneNumber(registerRequest.getPhoneNumber());
        userDTO.setRoleId(1); // Giả sử role "USER" có ID = 2
        userDTO.setIsActive(true);

        UserResponseDTO userResponse = userService.createUser(userDTO);

        String jwt = jwtUtil.generateToken(userResponse.getEmail(), userResponse.getRoleName());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userResponse.getEmail());

        return ResponseEntity.ok(new JwtResponseDTO(jwt, refreshToken.getToken(), userResponse.getEmail(), userResponse.getRoleName()));
    }

    @Operation(summary = "Refresh token", description = "Generate new JWT using refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired refresh token")
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequestDTO request) {
        String refreshTokenStr = request.getRefreshToken();
        RefreshToken refreshToken = refreshTokenService.findByToken(refreshTokenStr)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (!refreshTokenService.verifyExpiration(refreshToken)) {
            throw new RuntimeException("Refresh token has expired");
        }

        User user = refreshToken.getUser();
        String jwt = jwtUtil.generateToken(user.getEmail(), user.getRole().getName());
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        return ResponseEntity.ok(new RefreshTokenResponseDTO(jwt, newRefreshToken.getToken()));
    }
}