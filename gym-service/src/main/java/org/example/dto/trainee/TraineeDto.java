package org.example.dto.trainee;

import lombok.Data;
import org.example.entities.Trainer;
import org.example.entities.User;

import java.time.LocalDate;
import java.util.List;

@Data
public class TraineeDto {
    private LocalDate dateOfBirth;
    private String address;
    private User user;
    private List<Trainer> trainers;
}
