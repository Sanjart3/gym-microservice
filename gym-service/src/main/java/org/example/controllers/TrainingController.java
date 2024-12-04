package org.example.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.converters.TrainingConverter;
import org.example.dto.training.TrainingCreateRequest;
import org.example.services.TrainingService;
import org.example.exception.ValidatorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/{username}/training")
public class TrainingController {
    private static final Logger LOGGER = LogManager.getLogger(TrainingController.class);
    private final TrainingConverter trainingConverter;
    private final TrainingService trainingService;

    @Autowired
    public TrainingController(TrainingConverter trainingConverter, TrainingService trainingService) {
        this.trainingConverter = trainingConverter;
        this.trainingService = trainingService;
    }

    @PostMapping("create-training")
    public ResponseEntity<?> createTraining(@RequestBody TrainingCreateRequest trainingCreateRequest) {
        LOGGER.info("POST /api/training/create-training Create training initialized");
        try{
            trainingService.save(trainingConverter.fromTrainingCreateRequestToTraining(trainingCreateRequest), trainingCreateRequest.getTraineeUsername(), trainingCreateRequest.getTrainerUsername());
            LOGGER.info("POST /api/training/create-training: Status code 201 Created. Training creation completed");
            return ResponseEntity.ok().build();
        } catch (ValidatorException ve){
            LOGGER.error("POST /api/training/create-training: Status code: 400 Bad Request. Training creation failed", ve);
            return ResponseEntity.badRequest().build();
        }
    }
}
