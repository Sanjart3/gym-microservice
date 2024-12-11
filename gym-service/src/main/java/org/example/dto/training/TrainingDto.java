package org.example.dto.training;

import lombok.Data;
import org.example.entities.Trainee;
import org.example.entities.Trainer;
import org.example.entities.TrainingType;

import java.util.Date;

@Data
public class TrainingDto {
    private Trainee trainee;
    private Trainer trainer;
    private String trainingName;
    private TrainingType trainingType;
    private Date trainingDate;
    private int trainingDuration;
}
