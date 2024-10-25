package org.example.services;

import org.example.dto.AuthDto;
import org.example.entities.Training;

import java.util.List;

public interface TrainingService {
    List<Training> findAll();
    Training findById(Long id);
    Training save(Training training, String traineeUsername, String trainerUsername, AuthDto authDto);
}
