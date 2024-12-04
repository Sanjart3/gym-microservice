package org.example.services.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.enums.RoleType;
import org.example.repositories.TrainerRepository;
import org.example.dto.AuthDto;
import org.example.dto.CriteriaDto;
import org.example.dto.PasswordChangeDto;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.services.TrainerService;
import org.example.utils.PasswordGenerator;
import org.example.exception.NotFoundException;
import org.example.exception.ValidatorException;
import org.example.utils.validation.impl.TrainerValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setTrainerValidation(TrainerValidation trainerValidation) {
        this.trainerValidation = trainerValidation;
    }

    @Override
    public Trainer findByUsername(String username) {
        Optional<Trainer> trainerOptional = trainerRepository.findByUser_Username(username);
        if (trainerOptional.isPresent()) {
            return trainerOptional.get();
        }
        throw new NotFoundException("Trainer", username);
    }

    @Override
    public void changePassword(PasswordChangeDto passwordChangeDto) {
        Optional<Trainer> trainerOptional = trainerRepository.findByUser_Username(passwordChangeDto.getUsername());
        Trainer trainer = trainerOptional.get();
        trainer.getUser().setPassword(passwordChangeDto.getNewPassword());
        trainerRepository.save(trainer);
    }

    @Override
    public void changeStatus(String username, boolean status) {
        Optional<Trainer> trainerOptional = trainerRepository.findByUser_Username(username);
        Trainer trainer = trainerOptional.get();
        trainer.getUser().setIsActive(status);
    }

    @Override
    public AuthDto save(Trainer trainer) {
        try{
            trainerValidation.isValidForCreate(trainer);  //checks for validation, and throws exception for invalid parameters
            String username = getUsername(trainer.getUser().getFirstName()+trainer.getUser().getLastName());
            String password = passwordGenerator.generatePassword();
            trainer.getUser().setUsername(username);
            trainer.getUser().setPassword(passwordEncoder.encode(password));
            trainer.getUser().setRole(RoleType.ROLE_TRAINER);
            Trainer savedTrainer = trainerRepository.save(trainer);
            LOGGER.info("Saved trainer {}", savedTrainer);
            return new AuthDto(username, password);
        } catch (ValidatorException e){
            LOGGER.warn("Invalid trainer to save: {}", trainer, e);
            throw e;
        }
    }


    @Override
    public Trainer update(Trainer trainer) {
        try {
            trainerValidation.isValidForUpdate(trainer);  //checks for validation, and throws exception for invalid parameters
            Long id = trainerRepository.findIdByUser_Username(trainer.getUser().getUsername());
            trainer.setId(id);
            Trainer updatedTrainer = trainerRepository.save(trainer);
            LOGGER.info("Updated trainer {}", updatedTrainer);
            return updatedTrainer;
        } catch (ValidatorException e){
                LOGGER.warn("Invalid trainer to update: {}", trainer, e);
                throw e;
        }
    }

    @Override
    public List<Training> getTrainings(CriteriaDto criteriaDto, String username) {
        if (trainerRepository.findByUser_Username(username).isPresent()) {
            return trainerRepository.searchTrainerTraining(criteriaDto, username);
        }
        throw new NotFoundException("Trainer", username);
    }


    @Override
    public String getUsername(String basicUsername) {
        Long index = trainerRepository.countByUser_UsernameStartsWith(basicUsername);
        return basicUsername +"_" + index;
    }
}
