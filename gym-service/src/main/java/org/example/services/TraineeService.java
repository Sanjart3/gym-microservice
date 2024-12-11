package org.example.services;

import com.netflix.discovery.EurekaClient;
import lombok.extern.slf4j.Slf4j;
import org.example.enums.RoleType;
import org.example.repositories.TraineeRepository;
import org.example.dto.AuthDto;
import org.example.dto.CriteriaDto;
import org.example.dto.PasswordChangeDto;
import org.example.entities.Trainee;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.utils.PasswordGenerator;
import org.example.exception.NotFoundException;
import org.example.exception.ValidatorException;
import org.example.utils.validation.impl.TraineeValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TraineeService {

    @Autowired
    private EurekaClient eurekaClient;

    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private TraineeValidation traineeValidation;

    @Autowired
    private TrainerService trainerService;
    @Autowired
    private PasswordGenerator passwordGenerator;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Trainee findByUsername(String username) {
        Optional<Trainee> trainee = traineeRepository.findByUsername(username);
        if (trainee.isPresent()) {
            return trainee.get();
        } else {
            log.error("Trainee not found");
            throw new NotFoundException("Trainee", username);
        }
    }

    public void changePassword(PasswordChangeDto passwordChangeDto) {
        String username = passwordChangeDto.getUsername();
        Optional<Trainee> traineeOptional = traineeRepository.findByUsername(username);
        if (traineeOptional.isPresent()) {
            Trainee trainee = traineeOptional.get();
            trainee.getUser().setPassword(passwordEncoder.encode(passwordChangeDto.getNewPassword()));
            traineeRepository.save(trainee);
        } else {
            throw new NotFoundException("Trainee", username);
        }
    }

    public void changeStatus(String username, boolean status) {
        Optional<Trainee> traineeOptional = traineeRepository.findByUsername(username);
        if (traineeOptional.isPresent()) {
            Trainee trainee = traineeOptional.get();
            trainee.getUser().setIsActive(status);
        }
        throw new NotFoundException("Trainee", username);
    }

    public AuthDto save(Trainee trainee) {
        try {
            traineeValidation.isValidForCreate(trainee);  //checks for validation, and throws exception for invalid parameters
            String username = getUsername(trainee.getUser().getFirstName()+trainee.getUser().getLastName());
            String password = passwordGenerator.generatePassword();
            trainee.getUser().setUsername(username);
            trainee.getUser().setPassword(passwordEncoder.encode(password));
            trainee.getUser().setRole(RoleType.ROLE_TRAINEE);
            Trainee createdTrainee = traineeRepository.save(trainee);
            log.info("Trainee created: {}", createdTrainee);
            return new AuthDto(username, password);
        } catch (ValidatorException e) {
            log.warn("Trainee not created: {}", trainee, e);
            throw e;
        }
    }

    public Trainee update(Trainee trainee, String username) {
        Optional<Trainee> traineeOptional = traineeRepository.findByUsername(username);
        if (traineeOptional.isPresent()) {
            try{
                traineeValidation.isValidForUpdate(trainee); //checks for validation, and throws exception for invalid parameters
                Long id = traineeOptional.get().getId();
                trainee.setId(id);
                Trainee updatedTrainee = traineeRepository.save(trainee);
                log.info("Trainee updated: {}", updatedTrainee);
                return updatedTrainee;
            } catch (ValidatorException e) {
                log.warn("Trainee not updated: {}", trainee, e);
                throw e;
            }
        }
        throw new NotFoundException("Trainee", username);
    }

    public void deleteByUsername(String username) {
        if (traineeRepository.findByUsername(username).isPresent()) {
            traineeRepository.deleteByUser_Username(username);
            return;
        }
        throw new NotFoundException("Trainee", username);
    }


    public List<Trainer> findUnassignedTrainers(String username) {
        if (traineeRepository.findByUsername(username).isPresent()) {
            return traineeRepository.findUnAssignedTrainersByUsername(username);
        }
        throw new NotFoundException("Trainee", username);
    }

    public List<Trainer> updateTrainerList(String username, List<Trainer> trainers) {
        Optional<Trainee> trainee = traineeRepository.findByUsername(username);
        if (trainee.isPresent()) {
            trainers = trainers.stream().map(trainer -> trainerService.findByUsername(trainer.getUser().getUsername())).toList();
            trainee.get().setTrainers(trainers);
            traineeRepository.save(trainee.get());
            return trainers;
        }
        throw new NotFoundException("Trainee", username);
    }

    public List<Training> getTrainings(String username, CriteriaDto criteriaDto) {
        if (traineeRepository.findByUsername(username).isPresent()) {
            return traineeRepository.searchTraineeTraining(criteriaDto, username);
        } else {
            throw new NotFoundException("Trainee", username);
        }
    }


    public String getUsername(String basicUsername) {
        Long index = traineeRepository.countByUser_UsernameStartsWith(basicUsername);
        return basicUsername +"_" + index;
    }
}
