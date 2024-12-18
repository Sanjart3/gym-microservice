package org.example.services;

import lombok.extern.slf4j.Slf4j;
import org.example.messaging.TrainingEventSender;
import org.example.converters.TrainingConverter;
import org.example.externaldto.TrainingEventDto;
import org.example.repositories.TrainingRepository;
import org.example.entities.Training;
import org.example.exception.NotFoundException;
import org.example.exception.ValidatorException;
import org.example.utils.validation.impl.TrainingValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class TrainingService {

    @Autowired
    private TrainingRepository trainingRepository;

    private TrainingEventSender trainingEventSender;

    private TraineeService traineeService;

    private TrainerService trainerService;
    private TrainingValidation trainingValidation;
    private TrainingConverter trainingConverter;

    @Autowired
    public void setTrainingEventSender(TrainingEventSender trainingEventSender) {
        this.trainingEventSender = trainingEventSender;
    }

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

    public List<Training> findAll() {
        return trainingRepository.findAll();
    }

    public Training findById(Long id) {
        return trainingRepository.findById(id).get();
    }

    public TrainingEventDto save(Training training, String traineeUsername, String trainerUsername) {
        try{
            training.setTrainee(traineeService.findByUsername(traineeUsername));
            training.setTrainer(trainerService.findByUsername(trainerUsername));
            trainingValidation.isValidForCreate(training);  //checks for validation, and throws exception for invalid parameters
            Training savedTraining = trainingRepository.save(training);
            log.info("Saved training: {}", savedTraining);

            sendEvent(trainingConverter.fromTrainingToTrainingEventDto(savedTraining)); // sending the event to training-event service
            return trainingConverter.fromTrainingToTrainingEventDto(savedTraining);
        } catch (ValidatorException e) {
            log.warn("Invalid training: {}", training, e);
            throw e;
        }
    }

    public TrainingEventDto cancelTraining(String username, Long id){
        log.debug("Cancelling training with id: {}", id);

        Training training = trainingRepository.findTrainingByTraineeUserUsernameAndId(username, id)
                .orElseThrow(()-> new NotFoundException("Training", id+""));

        training.setIsDeleted(true);
        training.setDeletedDate(LocalDate.now());

        Training updatedTraining = trainingRepository.save(training);

        log.debug("Training cancelled successfully with ID: {}", id);

        return trainingConverter.fromTrainingToTrainingEventDto(updatedTraining);
    }

    private void sendEvent(TrainingEventDto trainingEventDto) {
        try{
            trainingEventSender.sendEvent(trainingEventDto);
        } catch (Exception e){
            log.warn("Error while sending event: {}", trainingEventDto, e);
        }
    }
}
