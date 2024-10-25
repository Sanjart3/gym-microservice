package org.example.trainingservice.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TrainingEventsDto {
    private String trainerUsername;
    private String trainerFirstName;
    private String trainerLastName;
    private Boolean isActive;
    private LocalDate trainingDate;
    private int trainingDuration;
    private String actionType;
}
