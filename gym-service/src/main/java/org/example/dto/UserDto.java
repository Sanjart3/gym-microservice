package org.example.dto;

import jakarta.validation.constraints.NotNull;

public class UserDto {
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    private String username;
    private String password;
    private String isActive;
}
