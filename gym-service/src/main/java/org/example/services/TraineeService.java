package org.example.services;

import org.example.dto.AuthDto;
import org.example.dto.CriteriaDto;
import org.example.dto.PasswordChangeDto;
import org.example.entities.Trainee;
import org.example.entities.Trainer;
import org.example.entities.Training;

import java.util.List;

public interface TraineeService {
    Trainee findByUsername(AuthDto auth, String username);
    void changePassword(AuthDto auth, PasswordChangeDto passwordChangeDto);

    void changeStatus(AuthDto auth, String username, boolean status);
    AuthDto save(Trainee trainee);
    Trainee update(AuthDto auth, Trainee trainee);
    Boolean deleteByUsername(AuthDto auth, String username);

    List<Trainer> findUnassignedTrainers(AuthDto auth, String username);
    List<Trainer> updateTrainerList(AuthDto auth, String username, List<Trainer> trainers);
    List<Training> getTrainings(AuthDto auth, String username, CriteriaDto criteriaDto);

    void authenticate(AuthDto auth);

    String getUsername(String basicUsername);
}
