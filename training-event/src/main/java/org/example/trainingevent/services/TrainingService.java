package org.example.trainingevent.services;

import org.example.trainingevent.dto.TrainerSummaryDto;
import org.example.trainingevent.dto.TrainingEventsDto;

import java.util.List;

public interface TrainingService {
    void saveTrainingEvent(TrainingEventsDto trainingEventsDto);

    List<TrainerSummaryDto> countTrainerSummary();
}
