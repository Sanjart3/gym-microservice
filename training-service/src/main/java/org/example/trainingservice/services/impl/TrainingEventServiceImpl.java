package org.example.trainingservice.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.trainingservice.dto.TrainingEventsDto;
import org.example.trainingservice.entities.TrainingEvents;
import org.example.trainingservice.repositories.TrainingEventRepository;
import org.example.trainingservice.services.TrainingService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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

        trainingEventRepository.save(trainingEvents);
        log.debug("Saved training event: {}", trainingEventsDto);
    }

    @Override
    public void deleteTrainingEvent(TrainingEventsDto trainingEventsDto) {
        log.debug("Deleting training event: {}", trainingEventsDto);

    }
}
