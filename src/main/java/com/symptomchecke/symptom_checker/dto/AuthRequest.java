package com.symptomchecke.symptom_checker.dto;

import lombok.Data;

import java.util.List;

@Data
public class AuthRequest {
    private String email;
    private String password;
    private int age;
    private String gender;
    private List<String> symptoms;
}

