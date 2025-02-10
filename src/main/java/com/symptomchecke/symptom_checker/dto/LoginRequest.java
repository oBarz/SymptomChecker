package com.symptomchecke.symptom_checker.dto;

import lombok.Data;

import java.util.List;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
