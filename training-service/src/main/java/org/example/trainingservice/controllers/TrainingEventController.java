package org.example.trainingservice.controllers;

import org.example.trainingservice.services.TrainingService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/training")
public class TrainingEventController {

    public final TrainingService trainingService;

    public TrainingEventController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }
}
