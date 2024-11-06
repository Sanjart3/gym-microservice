package org.example.services;

import org.example.dto.AuthDto;
import org.example.dto.CriteriaDto;
import org.example.dto.PasswordChangeDto;
import org.example.entities.Trainee;
import org.example.entities.Trainer;
import org.example.entities.Training;

import java.util.List;

public interface TraineeService {
    Trainee findByUsername(String username);
    void changePassword(PasswordChangeDto passwordChangeDto);

    void changeStatus(String username, boolean status);
    AuthDto save(Trainee trainee);
    Trainee update(Trainee trainee, String username);
    Boolean deleteByUsername(String username);

    List<Trainer> findUnassignedTrainers(String username);
    List<Trainer> updateTrainerList(String username, List<Trainer> trainers);
    List<Training> getTrainings(String username, CriteriaDto criteriaDto);

    String getUsername(String basicUsername);
}
