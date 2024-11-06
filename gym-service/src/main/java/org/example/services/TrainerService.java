package org.example.services;

import org.example.dto.AuthDto;
import org.example.dto.CriteriaDto;
import org.example.dto.PasswordChangeDto;
import org.example.entities.Trainer;
import org.example.entities.Training;

import java.util.List;

public interface TrainerService {
    Trainer findByUsername(String username);
    void changePassword(PasswordChangeDto passwordChangeDto);
    void changeStatus(String username, boolean status);
    AuthDto save(Trainer trainer);
    Trainer update(Trainer trainer);
    List<Training> getTrainings(CriteriaDto criteriaDto, String username);
    String getUsername(String basicUsername);
}
