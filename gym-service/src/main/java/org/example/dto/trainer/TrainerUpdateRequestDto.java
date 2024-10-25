package org.example.dto.trainer;

import lombok.Data;

@Data
public class TrainerUpdateRequestDto {
    private String firstName;
    private String lastName;
    private String username;
    private Boolean isActive;
    private Long specializationId;
}
