package org.example.entities;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Document(collection = "trainer_summary")
@Data
public class TrainerSummary {
    @MongoId
    private String username;

    @Indexed
    @Field("firstName")
    private String firstName;

    @Indexed
    @Field("lastName")
    private String lastName;

    @Field("isActive")
    private Boolean isActive;

    @Field("yearlySummaries")
    private Map<Integer, Map<Integer, Integer>> yearlySummary;

    public void addTraining(LocalDate date, @Positive int duration){
        int year = date.getYear();
        int month = date.getMonthValue();

        yearlySummary.computeIfAbsent(year, k -> new HashMap<>())
                .merge(month, duration, Integer::sum);
    }

    public void removeTraining(LocalDate date, int duration){
        int year = date.getYear();
        int month = date.getMonthValue();

        Map<Integer, Integer> monthlySummary = yearlySummary.get(year);
        if (monthlySummary != null) {
            int previousDuration = monthlySummary.get(month);
            int newDuration = duration - previousDuration;
             if (newDuration > 0) {
                 monthlySummary.put(month, newDuration);
             } else {
                 monthlySummary.remove(month);
                 if (monthlySummary.isEmpty()) {
                     yearlySummary.remove(year);
                 }
             }
        }
    }
}
