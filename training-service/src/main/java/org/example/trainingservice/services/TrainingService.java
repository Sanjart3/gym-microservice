package org.example.trainingservice.services;

import org.example.trainingservice.dto.TrainingEventsDto;

public interface TrainingService {
    void saveTrainingEvent(TrainingEventsDto trainingEventsDto);

    void deleteTrainingEvent(TrainingEventsDto trainingEventsDto);
}
