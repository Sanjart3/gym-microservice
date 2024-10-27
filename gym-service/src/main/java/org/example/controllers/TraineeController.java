package org.example.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.converters.TraineeConverter;
import org.example.dto.AuthDto;
import org.example.dto.PasswordChangeDto;
import org.example.dto.trainee.TraineeDto;
import org.example.dto.trainee.TraineeUpdateRequestDto;
import org.example.dto.training.TrainingDto;
import org.example.dto.training.TrainingEventDto;
import org.example.entities.Trainee;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.services.TraineeService;
import org.example.services.impl.TrainingServiceImpl;
import org.example.utils.ApiDescription;
import org.example.utils.TransactionLogger;
import org.example.utils.exception.AuthenticationException;
import org.example.utils.exception.NotFoundException;
import org.example.utils.exception.ValidatorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = "/api/trainee", consumes = {"application/json"}, produces = {"application/json", "application/XML"})
@Tag(name = ApiDescription.TRAINEE_TAG, description = "The trainee api")
public class TraineeController {
    private final Logger LOGGER = LogManager.getLogger(TraineeController.class);
    private final TraineeConverter traineeConverter;
    private final TraineeService traineeService;
    private final TrainingServiceImpl trainingServiceImpl;

    @Autowired
    public TraineeController(TraineeConverter traineeConverter, TraineeService traineeService, TrainingServiceImpl trainingServiceImpl) {
        this.traineeConverter = traineeConverter;
        this.traineeService = traineeService;
        this.trainingServiceImpl = trainingServiceImpl;
    }


    @Operation(summary = "Create user", tags = ApiDescription.TRAINEE_TAG)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = ApiDescription.TRAINEE_CREATED_SUCCESSFULLY),
            @ApiResponse(responseCode = "422", description = ApiDescription.UNPROCESSABLE_ENTITY)
    })
    @PostMapping("sign-up")
    public ResponseEntity<?> registerNewTrainee(@Parameter(description = "Created user object") @RequestBody TraineeDto traineeDto) {
        String transactionId = TransactionLogger.getTransactionId();
        LOGGER.info("[Transaction id: {}] POST /trainee/sign-up: Trainee sign-up initiated", transactionId);
        try {
            AuthDto loginInfo = traineeService.save(traineeConverter.toEntity(traineeDto));
            LOGGER.info("[Transaction id: {}] POST /api/trainee/sign-up  Status code: 201 Created", transactionId);
            return ResponseEntity.status(HttpStatus.CREATED).body(loginInfo);
        } catch (ValidatorException ve){
            LOGGER.error("[Transaction id: {}] POST /api/trainee/sign-up: Status code: 422 Unprocessable Trainee", transactionId, ve);
            return ResponseEntity.badRequest().body(ve);
        } finally {
            TransactionLogger.clear();
        }
    }

    @Operation(summary = "Trainee password change", description = ApiDescription.ONLY_LOGGED_IN_USER, tags = ApiDescription.TRAINEE_TAG)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = ApiDescription.PASSWORD_CHANGED_SUCCESSFULLY),
            @ApiResponse(responseCode = "422", description = ApiDescription.UNPROCESSABLE_ENTITY),
            @ApiResponse(responseCode = "401", description = ApiDescription.UNAUTHENTICATED_ACCESS)
    })
    @PutMapping("change-password")
    public ResponseEntity<String> changePassword(@RequestBody PasswordChangeDto passwordChangeDto) {
        String transactionId = TransactionLogger.getTransactionId();
        LOGGER.info("[Transaction id: {}] PUT /api/trainee/change-password: Trainee password change initiated", transactionId);
        try {
            traineeService.changePassword(passwordChangeDto);
            LOGGER.info("[Transaction id: {}] PUT /api/trainee/change-password: Status code: 200 OK", transactionId);
            return ResponseEntity.ok().body("Password changed successfully");
        } catch (NotFoundException e) {
            LOGGER.error("[Transaction id: {}] PUT /api/trainee/change-password: Status code: 404 Not found", transactionId, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } finally {
            TransactionLogger.clear();
        }
    }

    @Operation(summary = "Get Profile", description = ApiDescription.ONLY_LOGGED_IN_USER, tags = ApiDescription.TRAINEE_TAG)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = ApiDescription.PROFILE_RETRIEVED),
            @ApiResponse(responseCode = "404", description = ApiDescription.PROFILE_NOT_FOUND)
    })
    @GetMapping("{username}/profile")
    public ResponseEntity<?> getProfile(@PathVariable String username) {
        String transactionId = TransactionLogger.getTransactionId();
        LOGGER.info("[Transaction id: {}] GET /api/trainee/profile: Profile retrieval initiated for username: {}", transactionId, username);
        try {
            Trainee trainee = traineeService.findByUsername(username);
            LOGGER.info("[Transaction id: {}] GET /api/trainee/{}/profile: Status code: 200 OK. Profile retrieved for username: {}", transactionId, username, username);
            return ResponseEntity.ok().body(traineeConverter.toDto(trainee));
        } catch (NotFoundException e) {
            LOGGER.error("[Transaction id: {}] GET /api/trainee/{}/profile: Status code: 404 Not Found for username: {}", transactionId, username, username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } finally {
            TransactionLogger.clear();
        }
    }

    @Operation(summary = "Trainee profile update", description = ApiDescription.ONLY_LOGGED_IN_USER, tags = ApiDescription.TRAINEE_TAG)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = ApiDescription.PROFILE_UPDATED),
            @ApiResponse(responseCode = "404", description = ApiDescription.UNAUTHENTICATED_ACCESS),
            @ApiResponse(responseCode = "422", description = ApiDescription.UNPROCESSABLE_ENTITY)
    })
    @PutMapping("{username}/update-profile")
    public ResponseEntity<?> updateProfile(@PathVariable String username,
                                           @RequestBody TraineeUpdateRequestDto traineeRequestDto){
        String transactionId = TransactionLogger.getTransactionId();
        LOGGER.info("[Transaction id: {}] PUT /api/trainee/{}/update-profile: Trainee update profile initiated", transactionId, username);
        try {
            Trainee updatedTrainee = traineeService.update(traineeConverter.updateRequestDtoToTrainee(traineeRequestDto), username);
            LOGGER.info("[Transaction id: {}] PUT /api/trainee/{}/update-profile: Status code: 200 OK. Profile updated for username: {}", transactionId, username, username);
            return ResponseEntity.ok().body(traineeConverter.toDto(updatedTrainee));
        } catch (NotFoundException e) {
            LOGGER.error("[Transaction id: {}] PUT /api/trainee/{}/update-profile: Status code: 404 Not found. No trainee found for username: {}", transactionId, username, username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ValidatorException e) {
            LOGGER.error("[Transaction id: {}] PUT /api/trainee/{}/update-profile: Status code: 422 Unprocessable Trainee. Not valid trainee to update for username: {}", transactionId, username, username);
            return ResponseEntity.unprocessableEntity().body(e.getMessage());
        } finally {
            TransactionLogger.clear();
        }
    }

    @Operation(summary = "Delete Trainee", description = ApiDescription.ONLY_LOGGED_IN_USER, tags = ApiDescription.TRAINEE_TAG)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = ApiDescription.TRAINEE_DELETED),
            @ApiResponse(responseCode = "404", description = ApiDescription.UNAUTHENTICATED_ACCESS)
    })
    @DeleteMapping("{username}/delete")
    public ResponseEntity<Void> deleteProfile(@PathVariable String username) {
        String transactionId = TransactionLogger.getTransactionId();
        LOGGER.info("[Transaction id: {}] DELETE /api/trainee/{}/delete: Trainee delete initiated", transactionId, username);
        try {
            traineeService.deleteByUsername(username);
            LOGGER.info("[Transaction id: {}] DELETE /api/trainee/{}/delete: Status code: 204 No Content. Trainee deleted for username: {}", transactionId, username, username);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            LOGGER.error("[Transaction id: {}] DELETE /api/trainee/{}/delete: Status code: 404 Not found. No trainee found for username: {}", transactionId, username, username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } finally {
            TransactionLogger.clear();
        }
    }

    @Operation(summary = "Get Unassigned Trainers", description = ApiDescription.ONLY_LOGGED_IN_USER, tags = ApiDescription.TRAINEE_TAG)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = ApiDescription.TRAINERS_RETRIEVED),
            @ApiResponse(responseCode = "401", description = ApiDescription.UNAUTHENTICATED_ACCESS)
    })
    @GetMapping("{username}/not-assigned-trainers")
    public ResponseEntity<?> getNotAssignedTrainers(@PathVariable String username){
        String transactionId = TransactionLogger.getTransactionId();
        LOGGER.info("[Transaction id: {}] GET /api/trainee/{}/not-assigned-trainers: Retrieve not assigned trainers initiated", transactionId, username);
        try {
            List<Trainer> trainerList = traineeService.findUnassignedTrainers(username);
            LOGGER.info("[Transaction id: {}] GET /api/trainee/{}/not-assigned-trainers: Status code: 200 OK. Not assigned trainers retrieved for username: {}", transactionId, username, username);
            return ResponseEntity.ok(trainerList);
        } catch (NotFoundException e) {
            LOGGER.error("[Transaction id: {}] GET /api/trainee/{}/not-assigned-trainers: Status code: 404. Trainee not found for username: {}", transactionId, username, username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } finally {
            TransactionLogger.clear();
        }
    }

    @PutMapping("{username}/update-trainers")
    public ResponseEntity<Object> updateTrainerList(@PathVariable String username,
                                                     @RequestBody List<Trainer> trainerList) {
        String transactionId = TransactionLogger.getTransactionId();
        LOGGER.info("[Transaction id: {}] PUT /api/trainee/{}/update-trainers: Trainee trainer list update initiated", transactionId, username);
        try {
            List<Trainer> trainers = traineeService.updateTrainerList(username, trainerList);
            LOGGER.info("[Transaction id: {}] PUT /api/trainee/{}/update-trainers: Status code: 200 OK. Trainer list updated for username: {}", transactionId, username, username);
            return ResponseEntity.ok().body(trainers);
        } catch (NotFoundException e) {
            LOGGER.error("[Transaction id: {}] PUT /api/trainee/{}update-trainers: Status code: 404. Trainee not found for username: {}", transactionId, username, username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AuthenticationException e) {
            LOGGER.error("[Transaction id: {}] PUT /api/trainee/{}update-trainers: Status code: 401 UnAuthenticated. Could not authenticate for username: {}", transactionId, username, username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } finally {
            TransactionLogger.clear();
        }
    }

    @Operation(summary = "Get Trainee training list", description = ApiDescription.ONLY_LOGGED_IN_USER, tags = ApiDescription.TRAINEE_TAG)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = ApiDescription.TRAINING_LIST_RETRIEVED),
            @ApiResponse(responseCode = "401", description = ApiDescription.UNAUTHENTICATED_ACCESS)
    })
    @GetMapping("{username}/get-trainings")
    public ResponseEntity<?> getTraineeTrainings(@PathVariable String username,
                                                 @RequestParam LocalDate fromDate, @RequestParam LocalDate toDate, @RequestParam String trainingType) {
        String transactionId = TransactionLogger.getTransactionId();
        LOGGER.info("[Transaction id: {}] GET /api/trainee/{}/get-trainings: Retrieve trainings initiated", transactionId, username);
        try {
            List<Training> trainings = traineeService.getTrainings(username, traineeConverter.toCriteriaDto(fromDate, toDate, trainingType));
            LOGGER.info("[Transaction id: {}] GET /api/trainee/{}/get-trainings: Retrieve trainings was successful", transactionId, username);
            return ResponseEntity.ok().body(trainings);
        } catch (NotFoundException e) {
            LOGGER.error("[Transaction id: {}] GET /api/trainee/{}/get-trainings: Status code: 404. Training not found for username: {}", transactionId, username, username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AuthenticationException e) {
            LOGGER.error("[Transaction id: {}] GET /api/trainee/{}/get-trainings: Status code: 401 UnAuthenticated. Could not authenticate for username: {}", transactionId, username, username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @Operation(summary = "change status", description = ApiDescription.ONLY_LOGGED_IN_USER, tags = ApiDescription.TRAINEE_TAG)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = ApiDescription.STATUS_CHANGED),
            @ApiResponse(responseCode = "401", description = ApiDescription.UNAUTHENTICATED_ACCESS)
    })
    @PatchMapping("{username}/change-status")
    public ResponseEntity<?> changeStatus(@PathVariable String username,
                                          @RequestBody Boolean newStatus){
        String transactionId = TransactionLogger.getTransactionId();
        LOGGER.info("[Transaction id: {}] PATCH /api/trainee/{}/change-status: Trainee change status initiated", transactionId, username);
        try {
            traineeService.changeStatus(username, newStatus);
            LOGGER.info("[Transaction id: {}] PATCH /api/trainee/{}/change-status: Status code: 200 OK. Status change for username: {}", transactionId, username, username);
            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            LOGGER.error("[Transaction id: {}] PATCH /api/trainee/{}/change-status: Status code: 404 Not found. No trainee found for username: {}", transactionId, username, username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } finally {
            TransactionLogger.clear();
        }
    }

    @PostMapping("{username}/add-training/{trainer_username}")
    public ResponseEntity addTraining(@RequestBody TrainingDto trainingDto,
                                      @PathVariable("username") String traineeUsername,
                                      @PathVariable("trainer_username") String trainerUsername){
        String transactionId = TransactionLogger.getTransactionId();
        LOGGER.info("[Transaction id: {}] POST /api/trainee/{}/add-training", transactionId, trainingDto);
        try{
            TrainingEventDto trainingEventDto = trainingServiceImpl.save(trainingDto, traineeUsername, trainerUsername);
            LOGGER.info("Transaction id: {}] POST /api/trainee/{}/add-training Add training has been successful", transactionId, traineeUsername);
            return ResponseEntity.status(HttpStatus.CREATED).body(trainingEventDto);
        } catch (NotFoundException e) {
            LOGGER.error("[Transaction id: {}] POST /api/trainee/{}/add training Add training has been failed", transactionId, traineeUsername);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
