package com.example.bookstore.dto.request;

import lombok.Data;

@Data
public class UserDTO {
    private Integer roleId;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
}
