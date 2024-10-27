package org.example.services;

import org.example.dto.training.TrainingDto;
import org.example.dto.training.TrainingEventDto;
import org.example.entities.Training;

import java.util.List;

public interface TrainingService {
    List<Training> findAll();
    Training findById(Long id);
    TrainingEventDto save(TrainingDto trainingDto, String traineeUsername, String trainerUsername);
    TrainingEventDto cancelTraining(String username, Long id);
}
