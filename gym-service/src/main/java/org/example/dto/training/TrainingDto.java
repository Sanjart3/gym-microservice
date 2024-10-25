package org.example.dto.training;

import lombok.Data;
import org.example.entities.Trainee;
import org.example.entities.Trainer;
import org.example.entities.TrainingType;

import java.time.LocalDate;

@Data
public class TrainingDto {
    private Trainee trainee;
    private Trainer trainer;
    private String trainingName;
    private TrainingType trainingType;
    private LocalDate trainingDate;
    private int trainingDuration;
}
