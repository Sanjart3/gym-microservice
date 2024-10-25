package org.example.services.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.repositories.TrainerRepository;
import org.example.dto.AuthDto;
import org.example.dto.CriteriaDto;
import org.example.dto.PasswordChangeDto;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.services.TrainerService;
import org.example.utils.PasswordGenerator;
import org.example.utils.exception.AuthenticationException;
import org.example.utils.exception.NotFoundException;
import org.example.utils.exception.ValidatorException;
import org.example.utils.validation.impl.TrainerValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainerServiceImpl implements TrainerService {

    @Autowired
    private TrainerRepository trainerRepository;
    private TrainerValidation trainerValidation;
    private static final Logger LOGGER = LogManager.getLogger(TrainerServiceImpl.class);
    @Autowired
    private PasswordGenerator passwordGenerator;

    @Autowired
    public void setTrainerValidation(TrainerValidation trainerValidation) {
        this.trainerValidation = trainerValidation;
    }

    @Override
    public Trainer findByUsername(AuthDto auth, String username) {
        try {
            authenticate(auth);
            Optional<Trainer> trainerOptional = trainerRepository.findByUser_Username(username);
            if (trainerOptional.isPresent()) {
                return trainerOptional.get();
            }
            throw new NotFoundException("Trainer", username);
        } catch (AuthenticationException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public void changePassword(AuthDto auth, PasswordChangeDto passwordChangeDto) {
        Optional<Trainer> trainerOptional = trainerRepository.findByUser_Username(auth.getUsername());
        try {
            authenticate(auth);
            Trainer trainer = trainerOptional.get();
            trainer.getUser().setPassword(passwordChangeDto.getNewPassword());
            trainerRepository.save(trainer);
        } catch (AuthenticationException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public void changeStatus(AuthDto auth, String username, boolean status) {
        Optional<Trainer> trainerOptional = trainerRepository.findByUser_Username(username);
        try {
            authenticate(auth);
            Trainer trainer = trainerOptional.get();
            trainer.getUser().setIsActive(status);
        } catch (AuthenticationException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public AuthDto save(Trainer trainer) {
        try{
            trainerValidation.isValidForCreate(trainer);  //checks for validation, and throws exception for invalid parameters
            trainer.getUser().setUsername(trainer.getUser().getFirstName()+trainer.getUser().getLastName());
            trainer.getUser().setPassword(passwordGenerator.generatePassword());
            Trainer savedTrainer = trainerRepository.save(trainer);
            LOGGER.info("Saved trainer {}", savedTrainer);
            return new AuthDto(savedTrainer.getUser().getUsername(), savedTrainer.getUser().getPassword());
        } catch (ValidatorException e){
            LOGGER.warn("Invalid trainer to save: {}", trainer, e);
            throw e;
        }
    }


    @Override
    public Trainer update(AuthDto auth, Trainer trainer) {
        try {
            authenticate(auth);
            trainerValidation.isValidForUpdate(trainer);  //checks for validation, and throws exception for invalid parameters
            Long id = trainerRepository.findIdByUser_Username(trainer.getUser().getUsername());
            trainer.setId(id);
            Trainer updatedTrainer = trainerRepository.save(trainer);
            LOGGER.info("Updated trainer {}", updatedTrainer);
            return updatedTrainer;
        } catch (AuthenticationException e) {
            LOGGER.error(e.getMessage());
            throw e;
        } catch (ValidatorException e){
                LOGGER.warn("Invalid trainer to update: {}", trainer, e);
                throw e;
        }
    }

    @Override
    public List<Training> getTrainings(AuthDto auth, CriteriaDto criteriaDto, String username) {
        try {
            authenticate(auth);
            if (trainerRepository.findByUser_Username(username).isPresent()) {
                return trainerRepository.searchTrainerTraining(criteriaDto, username);
            }
            throw new NotFoundException("Trainer", username);
        } catch (AuthenticationException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public void authenticate(AuthDto auth) {
        if(trainerRepository.findByUser_UsernameAndUser_Password(auth.getUsername(), auth.getPassword()).isEmpty()){
            throw new AuthenticationException(auth.getUsername());
        }
    }

    @Override
    public String getUsername(String basicUsername) {
        Long index = trainerRepository.countByUser_UsernameStartsWith(basicUsername);
        return basicUsername +"_" + index;
    }
}
