package org.example.dto.trainer;

import lombok.Data;
import org.example.entities.Trainee;
import org.example.entities.User;

import java.util.List;

@Data
public class TrainerDto {
    private Long specialization;
    private User user;
    private List<Trainee> trainees;
}
