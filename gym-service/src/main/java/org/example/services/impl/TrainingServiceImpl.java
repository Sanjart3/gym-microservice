package org.example.services.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.repositories.TrainingRepository;
import org.example.dto.AuthDto;
import org.example.entities.Training;
import org.example.services.TraineeService;
import org.example.services.TrainerService;
import org.example.services.TrainingService;
import org.example.utils.exception.ValidatorException;
import org.example.utils.validation.impl.TrainingValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingServiceImpl implements TrainingService {

    private static final Logger LOGGER = LogManager.getLogger(TrainingServiceImpl.class);
    @Autowired
    private TrainingRepository trainingRepository;

    private TraineeService traineeService;
    private TrainerService trainerService;
    private TrainingValidation trainingValidation;
    @Autowired
    public void setTrainingValidation(TrainingValidation trainingValidation) {
        this.trainingValidation = trainingValidation;
    }
    @Autowired
    public void setTraineeService(TraineeService traineeService) {
        this.traineeService = traineeService;
    }
    @Autowired
    public void setTrainerService(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @Override
    public List<Training> findAll() {
        return trainingRepository.findAll();
    }

    @Override
    public Training findById(Long id) {
        return trainingRepository.findById(id).get();
    }

    @Override
    public Training save(Training training, String traineeUsername, String trainerUsername, AuthDto authDto) {
        try{
            training.setTrainee(traineeService.findByUsername(authDto, traineeUsername));
            training.setTrainer(trainerService.findByUsername(authDto, trainerUsername));
            trainingValidation.isValidForCreate(training);  //checks for validation, and throws exception for invalid parameters
            Training savedTraining = trainingRepository.save(training);
            LOGGER.info("Saved training: {}", savedTraining);
            return savedTraining;
        } catch (ValidatorException e) {
            LOGGER.warn("Invalid training: {}", training, e);
            throw e;
        }
    }
}
