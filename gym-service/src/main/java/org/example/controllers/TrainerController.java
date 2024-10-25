package org.example.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.converters.TrainerConverter;
import org.example.dto.AuthDto;
import org.example.dto.PasswordChangeDto;
import org.example.dto.trainer.TrainerDto;
import org.example.dto.trainer.TrainerUpdateRequestDto;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.services.TrainerService;
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
@RequestMapping("api/trainer")
@Tag(name = ApiDescription.TRAINER_TAG, description = "Operations related to trainers")
public class TrainerController {

    private final Logger LOGGER = LogManager.getLogger(TrainerController.class);
    private final TrainerConverter converter;
    private final TrainerService trainerService;

    @Autowired
    public TrainerController(TrainerConverter converter, TrainerService trainerService) {
        this.converter = converter;
        this.trainerService = trainerService;
    }

    @Operation(summary = "Sign up a new trainer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trainer successfully signed up"),
            @ApiResponse(responseCode = "400", description = "Bad request, validation errors or failed to create trainer")
    })
    @PostMapping("sign-up")
    public ResponseEntity<?> signUp(@Parameter(description = "Trainer details for sign-up") @RequestBody TrainerDto trainerDto) {
        String transactionId = TransactionLogger.getTransactionId();
        LOGGER.info("[Transaction id: {}] POST /api/trainer/sign-up: Trainer sign-up initiated", transactionId);
        try {
            AuthDto authDto = trainerService.save(converter.toEntity(trainerDto));
            LOGGER.info("[Transaction id: {}] POST /api/trainer/sign-up: Status code: 201 Created. Trainer sign-up successful for username: {}", transactionId, authDto.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(authDto);
        } catch (ValidatorException e) {
            LOGGER.error("[Transaction id: {}] POST /api/trainer/sign-up: Status code: 400 Bad Request. Trainer sign-up failed: {}", transactionId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } finally {
            TransactionLogger.clear();
        }
    }

    @Operation(summary = "Change trainer password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "404", description = "Trainer not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping("{username}/change-password")
    public ResponseEntity<String> changePassword(
            @PathVariable("username") String username,
            @RequestBody PasswordChangeDto passwordChangeDto) {
        String transactionId = TransactionLogger.getTransactionId();
        LOGGER.info("[Transaction id: {}] PUT /api/trainer/{}/change-password: Trainer change password initiated", transactionId, username);
        try {
            trainerService.changePassword(converter.fromPasswordChangeDtoToAuthDto(passwordChangeDto), passwordChangeDto);
            LOGGER.info("[Transaction id: {}] PUT /api/trainer/{}/change-password: Status code: 200 OK. Trainer change password successful for username: {}", transactionId, username, username);
            return ResponseEntity.ok("Password changed successfully");
        } catch (NotFoundException e) {
            LOGGER.error("[Transaction id: {}] PUT /api/trainer/{}/change-password: Status code: 404 Not Found. Password change unsuccessful for username: {}", transactionId, username, username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } finally {
            TransactionLogger.clear();
        }
    }

    @Operation(summary = "Get trainer profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = ApiDescription.PROFILE_RETRIEVED),
            @ApiResponse(responseCode = "404", description = ApiDescription.PROFILE_NOT_FOUND)
    })
    @GetMapping("{username}/profile")
    public ResponseEntity<TrainerDto> profile(
            @PathVariable("username") String username,
            @RequestHeader String authUsername,
            @RequestHeader String authPassword) {
        String transactionId = TransactionLogger.getTransactionId();
        LOGGER.info("[Transaction id: {}] GET /api/trainer/{}/profile: Trainer profile initiated", transactionId, username);
        try {
            Trainer trainer = trainerService.findByUsername(converter.toAuthDto(authUsername, authPassword), username);
            LOGGER.info("[Transaction id: {}] GET /api/trainer/{}/profile: Status code: 200 OK. Trainer profile successful for username: {}", transactionId, username, username);
            return ResponseEntity.ok(converter.toDto(trainer));
        } catch (NotFoundException e) {
            LOGGER.error("[Transaction id: {}] GET /api/trainer/{}/profile: Status code: 404 Not Found. Trainer profile not found for username: {}", transactionId, username, username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } finally {
            TransactionLogger.clear();
        }
    }

    @Operation(summary = "Update trainer profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = ApiDescription.PROFILE_UPDATED),
            @ApiResponse(responseCode = "404", description = ApiDescription.PROFILE_NOT_FOUND),
            @ApiResponse(responseCode = "422", description = ApiDescription.UNPROCESSABLE_ENTITY)
    })
    @PutMapping("{username}/update-profile")
    public ResponseEntity<TrainerDto> updateProfile(
            @PathVariable String username,
            @RequestHeader String authUsername,
            @RequestHeader String authPassword,
            @RequestBody TrainerUpdateRequestDto trainerUpdateRequestDto) {
        String transactionId = TransactionLogger.getTransactionId();
        LOGGER.info("[Transaction id: {}] PUT /api/trainer/{}/update-profile: Trainer profile update initiated", transactionId, username);
        try {
            Trainer trainer = trainerService.update(converter.toAuthDto(authUsername, authPassword), converter.fromTrainerUpdateRequestToTrainer(trainerUpdateRequestDto));
            LOGGER.info("[Transaction id: {}] PUT /api/trainer/{}/update-profile: Status code: 200 OK. Trainer profile update successful for username: {}", transactionId, username, username);
            return ResponseEntity.ok(converter.toDto(trainer));
        } catch (NotFoundException te) {
            LOGGER.error("[Transaction id: {}] PUT /api/trainer/{}/update-profile: Status code: 404 Not Found. No trainer found for username: {}", transactionId, username, username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (ValidatorException e) {
            LOGGER.error("[Transaction id: {}] PUT /api/trainer/{}/update-profile: Status code: 422 Unprocessable Entity. Not valid trainer to update for username: {}", transactionId, username, username);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null);
        } finally {
            TransactionLogger.clear();
        }
    }

    @Operation(summary = "Get Trainers Training List", description = ApiDescription.ONLY_LOGGED_IN_USER, tags = ApiDescription.TRAINER_TAG)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = ApiDescription.TRAINING_LIST_RETRIEVED),
            @ApiResponse(responseCode = "401", description = ApiDescription.UNAUTHENTICATED_ACCESS),
            @ApiResponse(responseCode = "404", description = ApiDescription.PROFILE_NOT_FOUND)
    })
    @GetMapping("{username}/get-trainings")
    public ResponseEntity<?> getTrainerTrainings(@PathVariable String username,
                                                 @RequestHeader String authUsername, @RequestHeader String authPassword,
                                                 @RequestParam LocalDate fromDate, @RequestParam LocalDate toDate, @RequestParam String trainingType) {
        String transactionId = TransactionLogger.getTransactionId();
        LOGGER.info("[Transaction id: {}] GET /api/trainer/{}/get-trainings: Trainer trainings retrieval initiated", transactionId, username);
        try {
            List<Training> trainings = trainerService.getTrainings(converter.toAuthDto(authUsername, authPassword), converter.toCriteriaDto(fromDate, toDate, trainingType), username);
            LOGGER.info("[Transaction id: {}] GET /api/trainer/{}/get-trainings: Status code: 200 OK. Training list retrieve for username: {}", transactionId, username, username);
            return ResponseEntity.ok().body(trainings);
        } catch (NotFoundException e) {
            LOGGER.error("[Transaction id: {}] GET /api/trainer/{}/get-trainings: Status code: 404 Not found. Trainer not found for username: {}", transactionId, username, username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AuthenticationException ae){
            LOGGER.error("[Transaction id: {}] GET /api/trainer/{}/get-trainings: Status code: 401 Unauthenticated. Could not authenticate for username: {}", transactionId, username, username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ae.getMessage());
        }
    }

    @Operation(summary = "Change trainer status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    @PatchMapping("{username}/change-status")
    public ResponseEntity<Void> changeStatus(
            @PathVariable String username,
            @RequestHeader String authUsername,
            @RequestHeader String authPassword,
            @RequestBody Boolean newStatus) {
        String transactionId = TransactionLogger.getTransactionId();
        LOGGER.info("[Transaction id: {}] PATCH /api/trainer/{}/change-status: Trainer status change initiated", transactionId, username);
        try {
            trainerService.changeStatus(converter.toAuthDto(authUsername, authPassword), username, newStatus);
            LOGGER.info("[Transaction id: {}] PATCH /api/trainer/{}/change-status: Status code: 200 OK. Status change for username: {}", transactionId, username, username);
            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            LOGGER.error("[Transaction id: {}] PATCH /api/trainer/{}/change-status: Status code: 404 Not found. No trainer found for username: {}", transactionId, username, username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } finally {
            TransactionLogger.clear();
        }
    }
}
