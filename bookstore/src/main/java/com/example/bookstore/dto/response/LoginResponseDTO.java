package com.example.bookstore.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDTO {
    private String token;
    private Integer userId;
    private String role;
}
