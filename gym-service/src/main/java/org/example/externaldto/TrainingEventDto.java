package org.example.externaldto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TrainingEventDto {
    @JsonProperty("username")
    private String trainerUsername;

    @JsonProperty("firstName")
    private String trainerFirstName;

    @JsonProperty("lastName")
    private String trainerLastName;

    @JsonProperty("isActive")
    private boolean isActive;

    @JsonProperty("trainingDate")
    private LocalDate trainingDate;

    @JsonProperty("trainingDuration")
    private int trainingDuration;

    @JsonProperty("actionType")
    private String actionType;
}