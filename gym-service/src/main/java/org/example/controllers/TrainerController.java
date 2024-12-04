package org.example.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.example.converters.TrainerConverter;
import org.example.dto.AuthDto;
import org.example.dto.PasswordChangeDto;
import org.example.dto.trainer.TrainerDto;
import org.example.dto.trainer.TrainerUpdateRequestDto;
import org.example.entities.Trainer;
import org.example.entities.Training;
import org.example.services.TrainerService;
import org.example.utils.ApiDescription;
import org.example.exception.AuthenticationException;
import org.example.exception.NotFoundException;
import org.example.exception.ValidatorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/trainer")
@Tag(name = ApiDescription.TRAINER_TAG, description = "Operations related to trainers")
public class TrainerController {

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
        log.info("POST /api/trainer/sign-up: Trainer sign-up initiated");
        try {
            AuthDto authDto = trainerService.save(converter.toEntity(trainerDto));
            log.info("POST /api/trainer/sign-up: Status code: 201 Created. Trainer sign-up successful for username: {}", authDto.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(authDto);
        } catch (ValidatorException e) {
            log.error("POST /api/trainer/sign-up: Status code: 400 Bad Request. Trainer sign-up failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
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
        log.info("PUT /api/trainer/{}/change-password: Trainer change password initiated", username);
        try {
            trainerService.changePassword(passwordChangeDto);
            log.info("PUT /api/trainer/{}/change-password: Status code: 200 OK. Trainer change password successful for username: {}", username, username);
            return ResponseEntity.ok("Password changed successfully");
        } catch (NotFoundException e) {
            log.error("PUT /api/trainer/{}/change-password: Status code: 404 Not Found. Password change unsuccessful for username: {}", username, username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Get trainer profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = ApiDescription.PROFILE_RETRIEVED),
            @ApiResponse(responseCode = "404", description = ApiDescription.PROFILE_NOT_FOUND)
    })
    @GetMapping("{username}/profile")
    public ResponseEntity<TrainerDto> profile(
            @PathVariable("username") String username) {
        log.info("GET /api/trainer/{}/profile: Trainer profile initiated", username);
        try {
            Trainer trainer = trainerService.findByUsername(username);
            log.info("GET /api/trainer/{}/profile: Status code: 200 OK. Trainer profile successful for username: {}", username, username);
            return ResponseEntity.ok(converter.toDto(trainer));
        } catch (NotFoundException e) {
            log.error("GET /api/trainer/{}/profile: Status code: 404 Not Found. Trainer profile not found for username: {}", username, username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
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
            @RequestBody TrainerUpdateRequestDto trainerUpdateRequestDto) {
        log.info("PUT /api/trainer/{}/update-profile: Trainer profile update initiated", username);
        try {
            Trainer trainer = trainerService.update(converter.fromTrainerUpdateRequestToTrainer(trainerUpdateRequestDto));
            log.info("PUT /api/trainer/{}/update-profile: Status code: 200 OK. Trainer profile update successful for username: {}", username, username);
            return ResponseEntity.ok(converter.toDto(trainer));
        } catch (NotFoundException te) {
            log.error("PUT /api/trainer/{}/update-profile: Status code: 404 Not Found. No trainer found for username: {}", username, username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (ValidatorException e) {
            log.error("PUT /api/trainer/{}/update-profile: Status code: 422 Unprocessable Entity. Not valid trainer to update for username: {}", username, username);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null);
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
                                                 @RequestParam LocalDate fromDate, @RequestParam LocalDate toDate, @RequestParam String trainingType) {
        log.info("GET /api/trainer/{}/get-trainings: Trainer trainings retrieval initiated", username);
        try {
            List<Training> trainings = trainerService.getTrainings(converter.toCriteriaDto(fromDate, toDate, trainingType), username);
            log.info("GET /api/trainer/{}/get-trainings: Status code: 200 OK. Training list retrieve for username: {}", username, username);
            return ResponseEntity.ok().body(trainings);
        } catch (NotFoundException e) {
            log.error("GET /api/trainer/{}/get-trainings: Status code: 404 Not found. Trainer not found for username: {}", username, username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AuthenticationException ae){
            log.error("GET /api/trainer/{}/get-trainings: Status code: 401 Unauthenticated. Could not authenticate for username: {}", username, username);
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
            @RequestBody Boolean newStatus) {
        log.info("PATCH /api/trainer/{}/change-status: Trainer status change initiated", username);
        try {
            trainerService.changeStatus(username, newStatus);
            log.info("PATCH /api/trainer/{}/change-status: Status code: 200 OK. Status change for username: {}", username, username);
            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            log.error("PATCH /api/trainer/{}/change-status: Status code: 404 Not found. No trainer found for username: {}", username, username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
