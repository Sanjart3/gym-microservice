package org.example.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CriteriaDto {
    private LocalDate fromDate;
    private LocalDate toDate;
    String trainingType;

    public CriteriaDto(LocalDate fromDate, LocalDate toDate, String trainingType) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.trainingType = trainingType;
    }
}
