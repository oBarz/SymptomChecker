package com.symptomchecke.symptom_checker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AnswerRequest {
    private String questionId;
    private String response;
}
