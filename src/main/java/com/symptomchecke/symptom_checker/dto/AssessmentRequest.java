package com.symptomchecke.symptom_checker.dto;

import lombok.Data;

import java.util.List;

@Data
public class AssessmentRequest {
    private String email;
    private List<String> initialSymptoms;
}
