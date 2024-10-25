package org.example.services;

import org.example.dto.AuthDto;
import org.example.dto.CriteriaDto;
import org.example.dto.PasswordChangeDto;
import org.example.entities.Trainer;
import org.example.entities.Training;

import java.util.List;

public interface TrainerService {
    Trainer findByUsername(AuthDto auth, String username);
    void changePassword(AuthDto auth, PasswordChangeDto passwordChangeDto);
    void changeStatus(AuthDto auth, String username, boolean status);
    AuthDto save(Trainer trainer);
    Trainer update(AuthDto auth, Trainer trainer);
    List<Training> getTrainings(AuthDto auth, CriteriaDto criteriaDto, String username);
    void authenticate(AuthDto auth);
    String getUsername(String basicUsername);
}
