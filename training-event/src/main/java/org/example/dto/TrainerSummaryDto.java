package org.example.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class TrainerSummaryDto {
    private String username;
    private String firstName;
    private String lastName;
    private boolean isActive;
    private Map<Integer, Map<Integer, Integer>> yearlySummary = new HashMap<>();
}
