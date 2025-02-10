package com.symptomchecke.symptom_checker.service;

import com.symptomchecke.symptom_checker.entity.Assessment;
import com.symptomchecke.symptom_checker.entity.Symptom;
import com.symptomchecke.symptom_checker.repository.AssessmentRepository;
import com.symptomchecke.symptom_checker.repository.SymptomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssessmentServiceTest {

    @Mock
    private AssessmentRepository assessmentRepository;

    @Mock
    private SymptomRepository symptomRepository;

    @InjectMocks
    private AssessmentService assessmentService;

    private Assessment testAssessment;
    private final String testEmail = "test@example.com";
    private final String testAssessmentId = UUID.randomUUID().toString();
    private final List<String> initialSymptoms = Arrays.asList("cough", "fever");

    @BeforeEach
    void setUp() {
        testAssessment = new Assessment(testAssessmentId, testEmail, new ArrayList<>(initialSymptoms), new HashMap<>());
    }

    @Test
    void testStartAssessment() {
        when(assessmentRepository.save(any(Assessment.class))).thenReturn(testAssessment);

        Assessment assessment = assessmentService.startAssessment(testEmail, initialSymptoms);

        assertNotNull(assessment);
        assertEquals(testEmail, assessment.getEmail());
        assertEquals(initialSymptoms, assessment.getSymptoms());
        verify(assessmentRepository, times(1)).save(any(Assessment.class));
    }

    @Test
    void testGetNextQuestion_WithUnansweredQuestion() {
        testAssessment.getResponses().put("Do you have a sore throat?", "yes");

        Symptom coughSymptom = new Symptom("cough", List.of("Do you have a sore throat?", "Are you experiencing shortness of breath?"), List.of("Flu", "COVID-19"));
        when(assessmentRepository.findById(testAssessmentId)).thenReturn(Optional.of(testAssessment));
        when(symptomRepository.findByName("cough")).thenReturn(Optional.of(coughSymptom));

        String nextQuestion = assessmentService.getNextQuestion(testAssessmentId);

        assertEquals("Are you experiencing shortness of breath?", nextQuestion);
    }

    @Test
    void testGetNextQuestion_AllAnswered() {
        testAssessment.getResponses().put("Do you have a sore throat?", "yes");
        testAssessment.getResponses().put("Are you experiencing shortness of breath?", "no");

        Symptom coughSymptom = new Symptom("cough", List.of("Do you have a sore throat?", "Are you experiencing shortness of breath?"), List.of("Flu", "COVID-19"));
        when(assessmentRepository.findById(testAssessmentId)).thenReturn(Optional.of(testAssessment));
        when(symptomRepository.findByName("cough")).thenReturn(Optional.of(coughSymptom));

        String nextQuestion = assessmentService.getNextQuestion(testAssessmentId);

        assertNull(nextQuestion);
    }

    @Test
    void testUpdateAssessment() {
        when(assessmentRepository.findById(testAssessmentId)).thenReturn(Optional.of(testAssessment));

        assessmentService.updateAssessment(testAssessmentId, "Do you have headaches?", "yes");

        assertTrue(testAssessment.getResponses().containsKey("Do you have headaches?"));
        assertEquals("yes", testAssessment.getResponses().get("Do you have headaches?"));

        verify(assessmentRepository, times(1)).save(testAssessment);
    }

    @Test
    void testUpdateAssessment_NotFound() {
        when(assessmentRepository.findById(testAssessmentId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                assessmentService.updateAssessment(testAssessmentId, "Do you have headaches?", "yes")
        );

        assertEquals("Assessment not found", exception.getMessage());
    }

    @Test
    void testGetFinalDiagnosis() {
        Symptom coughSymptom = new Symptom("cough", List.of("Do you have a sore throat?"), List.of("Flu"));
        Symptom feverSymptom = new Symptom("fever", List.of("Do you have body aches?"), List.of("COVID-19"));

        when(assessmentRepository.findAssessmentByAssessmentId(testAssessmentId)).thenReturn(Optional.of(testAssessment));
        when(symptomRepository.findByName("cough")).thenReturn(Optional.of(coughSymptom));
        when(symptomRepository.findByName("fever")).thenReturn(Optional.of(feverSymptom));

        String diagnosis = assessmentService.getFinalDiagnosis(testAssessmentId);

        assertEquals("COVID-19", diagnosis);
    }

    @Test
    void testGetFinalDiagnosis_UnknownCondition() {
        when(assessmentRepository.findAssessmentByAssessmentId(testAssessmentId)).thenReturn(Optional.of(testAssessment));
        when(symptomRepository.findByName(anyString())).thenReturn(Optional.empty());

        String diagnosis = assessmentService.getFinalDiagnosis(testAssessmentId);

        assertEquals("Unknown Condition", diagnosis);
    }

    @Test
    void testGetFinalDiagnosis_NoAssessmentFound() {
        when(assessmentRepository.findAssessmentByAssessmentId(testAssessmentId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                assessmentService.getFinalDiagnosis(testAssessmentId)
        );

        assertEquals("Assessment not found", exception.getMessage());
    }
}
