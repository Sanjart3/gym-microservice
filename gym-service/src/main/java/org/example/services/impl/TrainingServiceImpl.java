package org.example.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.converters.TrainingConverter;
import org.example.dto.training.TrainingEventDto;
import org.example.repositories.TrainingRepository;
import org.example.entities.Training;
import org.example.services.TraineeService;
import org.example.services.TrainerService;
import org.example.services.TrainingService;
import org.example.exception.NotFoundException;
import org.example.exception.ValidatorException;
import org.example.utils.validation.impl.TrainingValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class TrainingServiceImpl implements TrainingService {

    private static final Logger LOGGER = LogManager.getLogger(TrainingServiceImpl.class);
    @Autowired
    private TrainingRepository trainingRepository;

    private TraineeService traineeService;
    private TrainerService trainerService;
    private TrainingValidation trainingValidation;
    private TrainingConverter trainingConverter;
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
    @Autowired
    public void setTrainingConverter(TrainingConverter trainingConverter) {
        this.trainingConverter = trainingConverter;
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
    public TrainingEventDto save(Training training, String traineeUsername, String trainerUsername) {
        try{
            training.setTrainee(traineeService.findByUsername(traineeUsername));
            training.setTrainer(trainerService.findByUsername(trainerUsername));
            trainingValidation.isValidForCreate(training);  //checks for validation, and throws exception for invalid parameters
            Training savedTraining = trainingRepository.save(training);
            LOGGER.info("Saved training: {}", savedTraining);
            return trainingConverter.fromTrainingToTrainingEventDto(savedTraining);
        } catch (ValidatorException e) {
            LOGGER.warn("Invalid training: {}", training, e);
            throw e;
        }
    }

    public TrainingEventDto cancelTraining(String username, Long id){
        log.debug("Cancelling training with id: {}", id);

        Training training = trainingRepository.findTrainingByTraineeUserUsernameAndId(username, id)
                .orElseThrow(()-> new NotFoundException("Training", id+""));

        training.setIsDeleted(true);
        training.setDeletedDate(new Date());

        Training updatedTraining = trainingRepository.save(training);

        log.debug("Training cancelled successfully with ID: {}", id);

        return trainingConverter.fromTrainingToTrainingEventDto(updatedTraining);
    }
}
