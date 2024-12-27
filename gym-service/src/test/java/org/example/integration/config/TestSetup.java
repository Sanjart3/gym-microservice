package org.example.integration.config;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.example.entities.Trainee;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.entities.TrainingType;
import org.example.entities.User;
import org.example.enums.RoleType;
import org.example.enums.TrainingsType;
import org.example.repositories.TraineeRepository;
import org.example.repositories.TrainerRepository;
import org.example.repositories.TrainingRepository;
import org.example.repositories.TrainingTypeRepository;
import org.example.repositories.UserRepository;

import java.sql.Date;
import java.time.LocalDate;

import static org.example.integration.util.Constants.DEFAULT_TRAINEE_USERNAME;
import static org.example.integration.util.Constants.DEFAULT_TRAINER_USERNAME;

@RequiredArgsConstructor
public class TestSetup {
    private static final String DEFAULT_PASSWORD = "Password123";
    private static final String DEFAULT_FIRST_NAME = "Test";
    private static final String DEFAULT_LAST_NAME = "Test";
    private static final Date DEFAULT_DATE_OF_BIRTH = Date.valueOf("2000-01-01");
    private static final String DEFAULT_ADDRESS = "Test address";
    private static final String DEFAULT_TRAINING_NAME = "Test Training";

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final PasswordEncoder passwordEncoder;

    private static Trainer getTrainer(TrainingType trainingType, User user2) {
        Trainer trainer = new Trainer();
        trainer.setUser(user2);
        trainer.setTrainingType(trainingType);
        return trainer;
    }

    private static Trainee getTrainee(User user1) {
        Trainee trainee = new Trainee();
        trainee.setUser(user1);
        trainee.setDateOfBirth(DEFAULT_DATE_OF_BIRTH.toLocalDate());
        trainee.setAddress(DEFAULT_ADDRESS);
        return trainee;
    }

    @Before
    public void setUp() {
        Trainee savedtrainee = saveTrainee();

        TrainingType trainingType = trainingTypeRepository.getTrainingTypeByName(TrainingsType.BENCH_PRESS.toString())
                .orElseThrow(() -> new RuntimeException("TrainingType not found"));

        Trainer savedTrainer = getSavedTrainer(trainingType);

        saveTraining(savedTrainer, savedtrainee, trainingType);
    }

    @After
    @Transactional
    public void tearDown() {
        trainingRepository.deleteAllByTrainingName(DEFAULT_TRAINING_NAME);
        trainerRepository.deleteByUser_Username(DEFAULT_TRAINER_USERNAME);
        traineeRepository.deleteByUser_Username(DEFAULT_TRAINEE_USERNAME);
    }

    private void saveTraining(Trainer savedTrainer, Trainee savedtrainee, TrainingType trainingType) {
        Training training = new Training(savedtrainee, savedTrainer, DEFAULT_TRAINING_NAME, trainingType, LocalDate.now(), 60 );
        trainingRepository.save(training);
    }

    private Trainer getSavedTrainer(TrainingType trainingType) {
        User user2 = getUser(DEFAULT_TRAINER_USERNAME, RoleType.ROLE_TRAINER);
        Trainer trainer = getTrainer(trainingType, user2);
        return trainerRepository.save(trainer);
    }

    private Trainee saveTrainee() {
        User user1 = getUser(DEFAULT_TRAINEE_USERNAME, RoleType.ROLE_TRAINEE);
        Trainee trainee = getTrainee(user1);
        return traineeRepository.save(trainee);
    }

    private User getUser(String username, RoleType role) {
        User user1 = new User();
        user1.setUsername(username);
        user1.setFirstName(DEFAULT_FIRST_NAME);
        user1.setLastName(DEFAULT_LAST_NAME);
        user1.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        user1.setRole(role);
        user1.setIsActive(true);
        return user1;
    }


}