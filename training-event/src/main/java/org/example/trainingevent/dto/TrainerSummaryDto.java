package org.example.trainingevent.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class TrainerSummaryDto {
    private String trainerUsername;
    private String trainerFirstName;
    private String trainerLastName;
    private Boolean status;
    Map<Integer, Map<Integer, Integer>> period = new HashMap<>();

    public TrainerSummaryDto(String trainerUsername, String trainerFirstName, String trainerLastName, Boolean status) {
        this.trainerUsername = trainerUsername;
        this.trainerFirstName = trainerFirstName;
        this.trainerLastName = trainerLastName;
        this.status = status;
    }

    public void addTrainingTime(int year, int month, int duration) {
        period.computeIfAbsent(year, k -> new HashMap<>())
                .merge(month, duration, Integer::sum);
    }
}
