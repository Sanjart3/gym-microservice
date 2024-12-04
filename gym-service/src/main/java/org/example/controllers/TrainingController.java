package org.example.controllers;

import lombok.extern.slf4j.Slf4j;
import org.example.converters.TrainingConverter;
import org.example.dto.training.TrainingCreateRequest;
import org.example.services.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "api/{username}/training")
public class TrainingController {
    private final TrainingConverter trainingConverter;
    private final TrainingService trainingService;

    @Autowired
    public TrainingController(TrainingConverter trainingConverter, TrainingService trainingService) {
        this.trainingConverter = trainingConverter;
        this.trainingService = trainingService;
    }

    @PostMapping("create-training")
    public ResponseEntity<?> createTraining(@RequestBody TrainingCreateRequest trainingCreateRequest) {
        log.info("POST /api/training/create-training Create training initialized");
        trainingService.save(trainingConverter.fromTrainingCreateRequestToTraining(trainingCreateRequest), trainingCreateRequest.getTraineeUsername(), trainingCreateRequest.getTrainerUsername());
        log.info("POST /api/training/create-training: Status code 201 Created. Training creation completed");
        return ResponseEntity.ok().build();
    }
}
