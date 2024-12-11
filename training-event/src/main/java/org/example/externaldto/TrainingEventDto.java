package org.example.externaldto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
public class TrainingEventDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("username")
    private String trainerUsername;

    @JsonProperty("firstName")
    private String trainerFirstName;

    @JsonProperty("lastName")
    private String trainerLastName;

    @JsonProperty("isActive")
    private boolean isActive;

    @JsonProperty("trainingId")
    private Long id;

    @JsonProperty("trainingDate")
    private Date trainingDate;

    @JsonProperty("trainingDuration")
    private int trainingDuration;

    @JsonProperty("actionType")
    private String actionType;
}

