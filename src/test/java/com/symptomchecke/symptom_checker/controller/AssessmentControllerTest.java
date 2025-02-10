package com.symptomchecke.symptom_checker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symptomchecke.symptom_checker.config.SecurityConfig;
import com.symptomchecke.symptom_checker.dto.AssessmentRequest;
import com.symptomchecke.symptom_checker.entity.Assessment;
import com.symptomchecke.symptom_checker.service.AssessmentService;
import com.symptomchecke.symptom_checker.dto.AnswerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AssessmentController.class)
@Import(SecurityConfig.class)
class AssessmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AssessmentService assessmentService;

    @Autowired
    private ObjectMapper objectMapper;

    private Assessment mockAssessment;

    @BeforeEach
    void setUp() {
        mockAssessment = new Assessment(UUID.randomUUID().toString(), "test@example.com", List.of("cough"), Map.of());
    }

    @Test
    void testStartAssessment() throws Exception {
        AssessmentRequest request = new AssessmentRequest();
        request.setEmail("test@example.com");
        request.setInitialSymptoms(List.of("cough", "fever"));

        Mockito.when(assessmentService.startAssessment(anyString(), any())).thenReturn(mockAssessment);

        mockMvc.perform(post("/assessment/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testGetNextQuestion() throws Exception {
        Mockito.when(assessmentService.getNextQuestion(anyString())).thenReturn("Do you have a sore throat?");

        mockMvc.perform(get("/assessment/{assessmentId}/next-question", mockAssessment.getAssessmentId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Do you have a sore throat?"));
    }

    @Test
    void testUpdateAssessment() throws Exception {
        // Ensure "questionId" is the correct field name
        AnswerRequest request = new AnswerRequest("Do you have a headache?", "yes");

        mockMvc.perform(put("/assessment/{assessmentId}/answer", mockAssessment.getAssessmentId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))  // Ensure JSON matches the class
                .andExpect(status().isOk());

        Mockito.verify(assessmentService)
                .updateAssessment(mockAssessment.getAssessmentId(), "Do you have a headache?", "yes");
    }

    @Test
    void testGetFinalDiagnosis() throws Exception {
        Mockito.when(assessmentService.getFinalDiagnosis(anyString())).thenReturn("Flu");

        mockMvc.perform(get("/assessment/{assessmentId}/diagnosis", mockAssessment.getAssessmentId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Flu"));
    }
}
