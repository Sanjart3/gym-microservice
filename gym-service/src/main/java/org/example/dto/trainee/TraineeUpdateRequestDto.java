package org.example.dto.trainee;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TraineeUpdateRequestDto {
    private String username;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String address;
    private Boolean isActive;
}
