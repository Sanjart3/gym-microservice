package org.example.dto.training;

import lombok.Data;

import java.util.Date;

@Data
public class TrainingCreateRequest {
    private String traineeUsername;
    private String trainerUsername;
    private String trainingName;
    private Date trainingDate;
    private int trainingDuration;
}
