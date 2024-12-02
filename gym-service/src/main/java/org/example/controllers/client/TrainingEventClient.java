package org.example.controllers.client;

import org.example.config.FeignClientConfig;
import org.example.dto.training.TrainingEventDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "training-event", configuration = FeignClientConfig.class)
public interface TrainingEventClient {
    @RequestMapping("training/add")
    void addTrainingEvent(@RequestBody TrainingEventDto trainingEventDTO);
}
