package org.example.services;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.TrainerSummaryDto;
import org.example.externaldto.TrainingEventDto;
import org.example.entities.TrainerSummary;
import org.example.repositories.TrainingEventRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

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
        TrainerSummary trainerSummary = modelMapper.map(trainingEventDto, TrainerSummary.class);

        TrainerSummary existingTrainerSummary = trainingEventRepository.findByUsernameEquals(trainerSummary.getUsername());
        if (existingTrainerSummary != null) {
            existingTrainerSummary.addTraining(trainingEventDto.getTrainingDate(), trainingEventDto.getTrainingDuration());
            trainingEventRepository.save(existingTrainerSummary);
            log.debug("Updated training event for username: {}", trainingEventDto);
        } else {
            TrainerSummary savedTrainingEvent = trainingEventRepository.save(trainerSummary);
            log.debug("Saved training event for username: {}", savedTrainingEvent.getUsername());
        }

    }

    public List<TrainerSummaryDto> countTrainerSummary() {
        log.debug("Counting trainers' summary");
        List<TrainerSummary> trainingSummaries = trainingEventRepository.getAllBy();

        return trainingSummaries.stream()
                .map(
                        trainerSummary -> modelMapper.map(trainerSummary, TrainerSummaryDto.class)
                ).toList();
    }
}
