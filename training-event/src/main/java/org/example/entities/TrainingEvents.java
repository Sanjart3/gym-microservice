package org.example.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.example.enums.ActionType;

import java.time.LocalDate;

@Entity
@Data
public class TrainingEvents {
    @Id
    @GeneratedValue
    private Long id;

    private String trainerUsername;
    private String trainerFirstName;
    private String trainerLastName;
    private Boolean isActive;
    private LocalDate trainingDate;
    private int trainingDuration;
    @Enumerated(EnumType.STRING)
    private ActionType actionType;
}
