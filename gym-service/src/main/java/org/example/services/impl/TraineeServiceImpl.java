package org.example.services.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.repositories.TraineeRepository;
import org.example.dto.AuthDto;
import org.example.dto.CriteriaDto;
import org.example.dto.PasswordChangeDto;
import org.example.entities.Trainee;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.services.TraineeService;
import org.example.services.TrainerService;
import org.example.utils.PasswordGenerator;
import org.example.utils.exception.AuthenticationException;
import org.example.utils.exception.NotFoundException;
import org.example.utils.exception.ValidatorException;
import org.example.utils.validation.impl.TraineeValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TraineeServiceImpl implements TraineeService {

    private static final Logger LOGGER = LogManager.getLogger(TraineeServiceImpl.class);
    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private TraineeValidation traineeValidation;

    @Autowired
    private TrainerService trainerService;
    @Autowired
    private PasswordGenerator passwordGenerator;

    @Override
    public Trainee findByUsername(AuthDto auth, String username) {
        try{
            authenticate(auth);
            Optional<Trainee> trainee = traineeRepository.findByUsername(username);
            if (trainee.isPresent()) {
                return trainee.get();
            } else {
                LOGGER.error("Trainee not found");
                throw new NotFoundException("Trainee", username);
            }
        } catch (AuthenticationException e) {
            LOGGER.error(e.getMessage());
            throw new AuthenticationException(username);
        }
    }

    @Override
    public void changePassword(AuthDto auth, PasswordChangeDto passwordChangeDto) {
        String username = passwordChangeDto.getUsername();
        Optional<Trainee> traineeOptional = traineeRepository.findByUsername(username);
        if (traineeOptional.isPresent()) {
            try{
                authenticate(auth);
                Trainee trainee = traineeOptional.get();
                trainee.getUser().setPassword(passwordChangeDto.getNewPassword());
                traineeRepository.save(trainee);
            } catch (AuthenticationException e) {
                LOGGER.error(e.getMessage());
                throw new AuthenticationException(username);
            }
        } else {
            throw new NotFoundException("Trainee", username);
        }
    }

    @Override
    public void changeStatus(AuthDto auth, String username, boolean status) {
        Optional<Trainee> traineeOptional = traineeRepository.findByUsername(username);
        if (traineeOptional.isPresent()) {
            try {
                authenticate(auth);
                Trainee trainee = traineeOptional.get();
                trainee.getUser().setIsActive(status);
            } catch (AuthenticationException e) {
                LOGGER.error(e.getMessage());
                throw new AuthenticationException(username);
            }
        }
        throw new NotFoundException("Trainee", username);
    }

    @Override
    public AuthDto save(Trainee trainee) {
        try {
            traineeValidation.isValidForCreate(trainee);  //checks for validation, and throws exception for invalid parameters
            trainee.getUser().setUsername(getUsername(trainee.getUser().getFirstName()+trainee.getUser().getLastName()));
            trainee.getUser().setPassword(passwordGenerator.generatePassword());
            Trainee createdTrainee = traineeRepository.save(trainee);
            LOGGER.info("Trainee created: {}", createdTrainee);
            return new AuthDto(createdTrainee.getUser().getUsername(), createdTrainee.getUser().getPassword());
        } catch (ValidatorException e) {
            LOGGER.warn("Trainee not created: {}", trainee, e);
            throw e;
        }
    }

    @Override
    public Trainee update(AuthDto auth, Trainee trainee) {
        String username = auth.getUsername();
        Optional<Trainee> traineeOptional = traineeRepository.findByUsername(username);
        if (traineeOptional.isPresent()) {
            try{
                traineeValidation.isValidForUpdate(trainee); //checks for validation, and throws exception for invalid parameters
                authenticate(auth);
                Long id = traineeOptional.get().getId();
                trainee.setId(id);
                Trainee updatedTrainee = traineeRepository.save(trainee);
                LOGGER.info("Trainee updated: {}", updatedTrainee);
                return updatedTrainee;
            } catch (ValidatorException e) {
                LOGGER.warn("Trainee not updated: {}", trainee, e);
                throw e;
            } catch (AuthenticationException e) {
                LOGGER.error(e.getMessage());
                throw new AuthenticationException(username);
            }
        }
        throw new NotFoundException("Trainee", username);
    }

    @Override
    public Boolean deleteByUsername(AuthDto auth, String username) {
        try{
            authenticate(auth);
            if (traineeRepository.findByUsername(username).isPresent()) {
                return traineeRepository.deleteByUser_Username(username);
            }
            throw new NotFoundException("Trainee", username);
        } catch (AuthenticationException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
    }


    @Override
    public List<Trainer> findUnassignedTrainers(AuthDto auth, String username) {
        try {
            authenticate(auth);
            if (traineeRepository.findByUsername(username).isPresent()) {
                return traineeRepository.findUnAssignedTrainersByUsername(username);
            }
            throw new NotFoundException("Trainee", username);
        } catch (AuthenticationException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Trainer> updateTrainerList(AuthDto auth, String username, List<Trainer> trainers) {
        try {
            authenticate(auth);
            Optional<Trainee> trainee = traineeRepository.findByUsername(username);
            if (trainee.isPresent()) {
                trainers = trainers.stream().map(trainer -> trainerService.findByUsername(auth, trainer.getUser().getUsername())).toList();
                trainee.get().setTrainers(trainers);
                traineeRepository.save(trainee.get());
                return trainers;
            }
            throw new NotFoundException("Trainee", username);
        } catch (AuthenticationException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Training> getTrainings(AuthDto auth, String username, CriteriaDto criteriaDto) {
        try {
            authenticate(auth);
            if (traineeRepository.findByUsername(username).isPresent()) {
                return traineeRepository.searchTraineeTraining(criteriaDto, username);
            }
            throw new NotFoundException("Trainee", username);
        } catch (AuthenticationException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public void authenticate(AuthDto auth) {
        if(traineeRepository.findTraineeByUser_UsernameAndUser_Password(auth.getUsername(), auth.getPassword()).isEmpty()){
            throw new AuthenticationException(auth.getUsername());
        }
    }

    @Override
    public String getUsername(String basicUsername) {
        Long index = traineeRepository.countByUser_UsernameStartsWith(basicUsername);
        return basicUsername +"_" + index;
    }
}
