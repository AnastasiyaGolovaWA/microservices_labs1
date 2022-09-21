package com.example.springclient.models;

import lombok.Data;

@Data
public class UserDTO {
    private String id;
    private String username;
    private String email;
    private String password;
    private String firstname;
    private String lastname;
    private String role;
}