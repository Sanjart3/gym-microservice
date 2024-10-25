package org.example.repositories.training;

import org.example.dto.CriteriaDto;
import org.example.entities.Training;

import java.util.List;

public interface UserTrainingRepository {
    List<Training> searchTraineeTraining(CriteriaDto criteriaDto, String username);
    List<Training> searchTrainerTraining(CriteriaDto criteriaDto, String username);
}
