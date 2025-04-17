package com.example.bookstore.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RoleResponseDTO {
    private Integer id;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
