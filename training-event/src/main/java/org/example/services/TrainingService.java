package org.example.services;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.TrainerSummaryDto;
import org.example.externaldto.TrainingEventDto;
import org.example.entities.TrainingEvents;
import org.example.enums.ActionType;
import org.example.repositories.TrainingEventRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class TrainingService {
    private final TrainingEventRepository trainingEventRepository;
    private final ModelMapper modelMapper;

    public TrainingService(TrainingEventRepository trainingEventRepository, ModelMapper modelMapper) {
        this.trainingEventRepository = trainingEventRepository;
        this.modelMapper = modelMapper;
    }

    public void saveTrainingEvent(TrainingEventDto trainingEventDto) {
        log.debug("Saving training event: {}", trainingEventDto);
        TrainingEvents trainingEvents = modelMapper.map(trainingEventDto, TrainingEvents.class);

        TrainingEvents savedTrainingEvent = trainingEventRepository.save(trainingEvents);
        log.debug("Saved training event with ID: {}", savedTrainingEvent.getId());
    }

    public List<TrainerSummaryDto> countTrainerSummary() {
        log.debug("Counting trainers' summary");
        List<TrainingEvents> trainingEvents = trainingEventRepository.findAllByActionType(ActionType.ADD);
        Map<String, TrainerSummaryDto> trainerSummaries = new HashMap<>();
        for (TrainingEvents trainingEvent : trainingEvents) {
            TrainerSummaryDto trainerSummary = trainerSummaries.getOrDefault(trainingEvent.getTrainerUsername(), new TrainerSummaryDto(
                    trainingEvent.getTrainerUsername(),
                    trainingEvent.getTrainerFirstName(),
                    trainingEvent.getTrainerLastName(),
                    trainingEvent.getIsActive()
            ));
            Integer year = getYear(trainingEvent);
            Integer month = getMonth(trainingEvent);

            trainerSummary.addTrainingTime(year, month, trainingEvent.getTrainingDuration()); // Adding Duration
            trainerSummaries.put(trainingEvent.getTrainerUsername(), trainerSummary);
        }

        return new ArrayList<>(trainerSummaries.values());
    }

    public Integer getYear(TrainingEvents trainingEvent) {
        LocalDate date = trainingEvent.getTrainingDate();
        return date.getYear();
    }

    public Integer getMonth(TrainingEvents trainingEvent) {
        LocalDate date = trainingEvent.getTrainingDate();
        return date.getMonthValue();
    }
}
