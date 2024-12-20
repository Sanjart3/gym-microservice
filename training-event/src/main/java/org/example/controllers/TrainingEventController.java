package org.example.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.TrainerSummaryDto;
import org.example.externaldto.TrainingEventDto;
import org.example.services.TrainingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/event")
@Slf4j
public class TrainingEventController {

    public final TrainingService trainingService;

    public TrainingEventController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @Operation(summary = "Create a new training event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Training event created successfully"),
            @ApiResponse(responseCode = "403", description = "Bad request")
    })
    @PostMapping
    public ResponseEntity<String> addTrainingEvent(@RequestBody TrainingEventDto trainingEventDto) {
        log.info("Add training event: {}", trainingEventDto);
        trainingService.saveTrainingEvent(trainingEventDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get summaries of trainers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved Trainers' summaries successfully!")
    })
    @GetMapping("summary")
    public ResponseEntity<List<TrainerSummaryDto>> getTrainingEvents() {
        List<TrainerSummaryDto> trainerSummaryList = trainingService.countTrainerSummary();
        return ResponseEntity.ok(trainerSummaryList);
    }

}
