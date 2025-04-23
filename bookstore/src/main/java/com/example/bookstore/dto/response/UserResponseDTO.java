package com.example.bookstore.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponseDTO {
    private Integer id;
    private Integer roleId;
    private String roleName;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
