package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PasswordChangeDto {
    private String username;
    private String oldPassword;
    private String newPassword;
}
