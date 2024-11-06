package org.example.trainingevent.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.trainingevent.dto.TrainerSummaryDto;
import org.example.trainingevent.dto.TrainingEventsDto;
import org.example.trainingevent.entities.TrainingEvents;
import org.example.trainingevent.enums.ActionType;
import org.example.trainingevent.repositories.TrainingEventRepository;
import org.example.trainingevent.services.TrainingService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class TrainingEventServiceImpl implements TrainingService {
    private final TrainingEventRepository trainingEventRepository;
    private final ModelMapper modelMapper;

    public TrainingEventServiceImpl(TrainingEventRepository trainingEventRepository, ModelMapper modelMapper) {
        this.trainingEventRepository = trainingEventRepository;
        this.modelMapper = modelMapper;
    }

    public void saveTrainingEvent(TrainingEventsDto trainingEventsDto) {
        log.debug("Saving training event: {}", trainingEventsDto);
        TrainingEvents trainingEvents = modelMapper.map(trainingEventsDto, TrainingEvents.class);

        TrainingEvents savedTrainingEvent = trainingEventRepository.save(trainingEvents);
        log.debug("Saved training event with ID: {}", savedTrainingEvent.getId());
    }

    @Override
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
