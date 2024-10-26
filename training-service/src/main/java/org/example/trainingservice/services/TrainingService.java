package org.example.trainingservice.services;

import org.example.trainingservice.dto.TrainerSummaryDto;
import org.example.trainingservice.dto.TrainingEventsDto;

import java.util.List;

public interface TrainingService {
    void saveTrainingEvent(TrainingEventsDto trainingEventsDto);

    List<TrainerSummaryDto> countTrainerSummary();
}
