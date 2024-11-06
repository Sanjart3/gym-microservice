package org.example.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.converters.TrainingConverter;
import org.example.dto.AuthDto;
import org.example.dto.training.TrainingCreateRequest;
import org.example.services.TrainingService;
import org.example.utils.TransactionLogger;
import org.example.utils.exception.ValidatorException;
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
        String transactionId = TransactionLogger.getTransactionId();
        LOGGER.info("[TransactionId: {}] POST /api/training/create-training Create training initialized", transactionId);
        try{
            trainingService.save(trainingConverter.fromTrainingCreateRequestToTraining(trainingCreateRequest), trainingCreateRequest.getTraineeUsername(), trainingCreateRequest.getTrainerUsername());
            LOGGER.info("[Transaction id: {}] POST /api/training/create-training: Status code 201 Created. Training creation completed", transactionId);
            return ResponseEntity.ok().build();
        } catch (ValidatorException ve){
            LOGGER.error("[Transaction id: {}] POST /api/training/create-training: Status code: 400 Bad Request. Training creation failed", transactionId, ve);
            return ResponseEntity.badRequest().build();
        } finally {
            TransactionLogger.clear();
        }
    }
}
