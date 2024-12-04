package org.example.controllers;

import feign.FeignException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.example.client.TrainingEventClient;
import org.example.converters.TraineeConverter;
import org.example.dto.AuthDto;
import org.example.dto.PasswordChangeDto;
import org.example.dto.trainee.TraineeDto;
import org.example.dto.trainee.TraineeUpdateRequestDto;
import org.example.dto.training.TrainingEventDto;
import org.example.entities.Trainee;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.services.TraineeService;
import org.example.services.TrainingService;
import org.example.utils.ApiDescription;
import org.example.exception.AuthenticationException;
import org.example.exception.NotFoundException;
import org.example.exception.ValidatorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/trainee", produces = {"application/json", "application/XML"})
@Tag(name = ApiDescription.TRAINEE_TAG, description = "The trainee api")
public class TraineeController {
    private final TrainingEventClient trainingEventClient;
    private final TraineeConverter traineeConverter;
    private final TraineeService traineeService;
    private final TrainingService trainingService;

    @Autowired
    public TraineeController(TrainingEventClient trainingEventClient, TraineeConverter traineeConverter, TraineeService traineeService, TrainingService trainingService) {
        this.trainingEventClient = trainingEventClient;
        this.traineeConverter = traineeConverter;
        this.traineeService = traineeService;
        this.trainingService = trainingService;
    }


    @Operation(summary = "Create user", tags = ApiDescription.TRAINEE_TAG)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = ApiDescription.TRAINEE_CREATED_SUCCESSFULLY),
            @ApiResponse(responseCode = "422", description = ApiDescription.UNPROCESSABLE_ENTITY)
    })
    @PostMapping("sign-up")
    public ResponseEntity<?> registerNewTrainee(@Parameter(description = "Created user object") @RequestBody TraineeDto traineeDto) {
        log.info("POST /trainee/sign-up: Trainee sign-up initiated");
        try {
            AuthDto loginInfo = traineeService.save(traineeConverter.toEntity(traineeDto));
            log.info("POST /api/trainee/sign-up  Status code: 201 Created");
            return ResponseEntity.status(HttpStatus.CREATED).body(loginInfo);
        } catch (ValidatorException ve){
            log.error("POST /api/trainee/sign-up: Status code: 422 Unprocessable Trainee", ve);
            return ResponseEntity.badRequest().body(ve);
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
        log.info("PUT /api/trainee/change-password: Trainee password change initiated");
        try {
            traineeService.changePassword(passwordChangeDto);
            log.info("PUT /api/trainee/change-password: Status code: 200 OK");
            return ResponseEntity.ok().body("Password changed successfully");
        } catch (NotFoundException e) {
            log.error("PUT /api/trainee/change-password: Status code: 404 Not found", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Get Profile", description = ApiDescription.ONLY_LOGGED_IN_USER, tags = ApiDescription.TRAINEE_TAG)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = ApiDescription.PROFILE_RETRIEVED),
            @ApiResponse(responseCode = "404", description = ApiDescription.PROFILE_NOT_FOUND)
    })
    @GetMapping("{username}/profile")
    public ResponseEntity<?> getProfile(@PathVariable String username) {
        log.info("[GET /api/trainee/profile: Profile retrieval initiated for username: {}", username);
        try {
            Trainee trainee = traineeService.findByUsername(username);
            log.info("GET /api/trainee/{}/profile: Status code: 200 OK. Profile retrieved for username: {}", username, username);
            return ResponseEntity.ok().body(traineeConverter.toDto(trainee));
        } catch (NotFoundException e) {
            log.error("GET /api/trainee/{}/profile: Status code: 404 Not Found for username: {}", username, username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
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
        log.info("PUT /api/trainee/{}/update-profile: Trainee update profile initiated", username);
        try {
            Trainee updatedTrainee = traineeService.update(traineeConverter.updateRequestDtoToTrainee(traineeRequestDto), username);
            log.info("PUT /api/trainee/{}/update-profile: Status code: 200 OK. Profile updated for username: {}", username, username);
            return ResponseEntity.ok().body(traineeConverter.toDto(updatedTrainee));
        } catch (NotFoundException e) {
            log.error("PUT /api/trainee/{}/update-profile: Status code: 404 Not found. No trainee found for username: {}", username, username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ValidatorException e) {
            log.error("PUT /api/trainee/{}/update-profile: Status code: 422 Unprocessable Trainee. Not valid trainee to update for username: {}", username, username);
            return ResponseEntity.unprocessableEntity().body(e.getMessage());
        }
    }

    @Operation(summary = "Delete Trainee", description = ApiDescription.ONLY_LOGGED_IN_USER, tags = ApiDescription.TRAINEE_TAG)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = ApiDescription.TRAINEE_DELETED),
            @ApiResponse(responseCode = "404", description = ApiDescription.UNAUTHENTICATED_ACCESS)
    })
    @DeleteMapping("{username}/delete")
    public ResponseEntity<Void> deleteProfile(@PathVariable String username) {
        log.info("DELETE /api/trainee/{}/delete: Trainee delete initiated", username);
        try {
            traineeService.deleteByUsername(username);
            log.info("DELETE /api/trainee/{}/delete: Status code: 204 No Content. Trainee deleted for username: {}", username, username);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            log.error("DELETE /api/trainee/{}/delete: Status code: 404 Not found. No trainee found for username: {}", username, username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Operation(summary = "Get Unassigned Trainers", description = ApiDescription.ONLY_LOGGED_IN_USER, tags = ApiDescription.TRAINEE_TAG)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = ApiDescription.TRAINERS_RETRIEVED),
            @ApiResponse(responseCode = "401", description = ApiDescription.UNAUTHENTICATED_ACCESS)
    })
    @GetMapping("{username}/not-assigned-trainers")
    public ResponseEntity<?> getNotAssignedTrainers(@PathVariable String username){
        log.info("GET /api/trainee/{}/not-assigned-trainers: Retrieve not assigned trainers initiated", username);
        try {
            List<Trainer> trainerList = traineeService.findUnassignedTrainers(username);
            log.info("GET /api/trainee/{}/not-assigned-trainers: Status code: 200 OK. Not assigned trainers retrieved for username: {}", username, username);
            return ResponseEntity.ok(trainerList);
        } catch (NotFoundException e) {
            log.error("GET /api/trainee/{}/not-assigned-trainers: Status code: 404. Trainee not found for username: {}", username, username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("{username}/update-trainers")
    public ResponseEntity<Object> updateTrainerList(@PathVariable String username,
                                                     @RequestBody List<Trainer> trainerList) {
        log.info("PUT /api/trainee/{}/update-trainers: Trainee trainer list update initiated", username);
        try {
            List<Trainer> trainers = traineeService.updateTrainerList(username, trainerList);
            log.info("PUT /api/trainee/{}/update-trainers: Status code: 200 OK. Trainer list updated for username: {}", username, username);
            return ResponseEntity.ok().body(trainers);
        } catch (NotFoundException e) {
            log.error("PUT /api/trainee/{}update-trainers: Status code: 404. Trainee not found for username: {}", username, username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AuthenticationException e) {
            log.error("PUT /api/trainee/{}update-trainers: Status code: 401 UnAuthenticated. Could not authenticate for username: {}", username, username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
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
        log.info("GET /api/trainee/{}/get-trainings: Retrieve trainings initiated", username);
        try {
            List<Training> trainings = traineeService.getTrainings(username, traineeConverter.toCriteriaDto(fromDate, toDate, trainingType));
            log.info("GET /api/trainee/{}/get-trainings: Retrieve trainings was successful", username);
            return ResponseEntity.ok().body(trainings);
        } catch (NotFoundException e) {
            log.error("GET /api/trainee/{}/get-trainings: Status code: 404. Training not found for username: {}", username, username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AuthenticationException e) {
            log.error("GET /api/trainee/{}/get-trainings: Status code: 401 UnAuthenticated. Could not authenticate for username: {}", username, username);
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
        log.info("PATCH /api/trainee/{}/change-status: Trainee change status initiated", username);
        try {
            traineeService.changeStatus(username, newStatus);
            log.info("PATCH /api/trainee/{}/change-status: Status code: 200 OK. Status change for username: {}", username, username);
            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            log.error("PATCH /api/trainee/{}/change-status: Status code: 404 Not found. No trainee found for username: {}", username, username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Add Training", description = "Adds a training to the trainee's profile.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Training added successfully"),
            @ApiResponse(responseCode = "400", description = "JSON is invalid and has some error in validation"),
    })
    @PostMapping("{username}/add-training/{trainer_username}")
    public ResponseEntity addTraining(@RequestBody Training training,
                                      @PathVariable("username") String traineeUsername,
                                      @PathVariable("trainer_username") String trainerUsername){
        log.info("POST /api/trainee/{}/add-training", training);
        try{
            TrainingEventDto trainingEventDto = trainingService.save(training, traineeUsername, trainerUsername);
            log.info("POST /api/trainee/{}/add-training Add training has been successful", traineeUsername);
            // Working with Training event
            addTrainingEvent(trainingEventDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(trainingEventDto);
        } catch (NotFoundException e) {
            log.error("POST /api/trainee/{}/add training Add training has been failed", traineeUsername);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    public void addTrainingEvent(TrainingEventDto trainingEventDto){
        try{
            trainingEventClient.addTrainingEvent(trainingEventDto);
        } catch (FeignException e) {
            log.warn("Tracking service is not available: {}", e.getMessage());
        }
    }

    @DeleteMapping("/{username}/training")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Cancel Training", description = "Cancels a training")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Training deleted successfully"),
    })
    public void cancelTraining(@RequestBody long trainingId,
                               @PathVariable("username") String username) {

        Trainee trainee = traineeService.findByUsername(username);
        trainingService.cancelTraining(trainee.getUser().getUsername(), trainingId);
    }
}
