package com.symptomchecke.symptom_checker.service;

import com.symptomchecke.symptom_checker.entity.Assessment;
import com.symptomchecke.symptom_checker.entity.Symptom;
import com.symptomchecke.symptom_checker.repository.AssessmentRepository;
import com.symptomchecke.symptom_checker.repository.SymptomRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class AssessmentService {

    private static final Logger logger = LoggerFactory.getLogger(AssessmentService.class);

    private final AssessmentRepository assessmentRepository;
    private final SymptomRepository symptomRepository;

    public AssessmentService(AssessmentRepository assessmentRepository, SymptomRepository symptomRepository) {
        this.assessmentRepository = assessmentRepository;
        this.symptomRepository = symptomRepository;
    }

    public Assessment startAssessment(String email, List<String> initialSymptoms) {
        Assessment assessment = new Assessment(UUID.randomUUID().toString(), email, initialSymptoms, new HashMap<>());
        assessmentRepository.save(assessment);
        logger.info("Started assessment for email: {} with symptoms: {}", email, initialSymptoms);
        return assessment;
    }

    public String getNextQuestion(String assessmentId) {
        logger.info("Fetching next question for assessmentId: {}", assessmentId);
        Assessment assessment = assessmentRepository.findAssessmentByAssessmentId(assessmentId)
                .orElseThrow(() -> new RuntimeException("Assessment not found" ));


        for (String symptom : assessment.getSymptoms()) {
            Optional<Symptom> symptomData = symptomRepository.findByName(symptom);
            if (symptomData.isPresent()) {
                List<String> questions = symptomData.get().getQuestions();
                for (String question : questions) {
                    if (!assessment.getResponses().containsKey(question)) {
                        logger.info("Next question: {}", question);
                        return question;
                    }
                }
            }
        }
        logger.warn("No more questions available for assessmentId: {}", assessmentId);
        return null;
    }

    public void updateAssessment(String assessmentId, String question, String response) {
        logger.info("Updating assessment: {} with response to question: {}", assessmentId, question);
        Assessment assessment = assessmentRepository.findAssessmentByAssessmentId(assessmentId)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));

        assessment.getResponses().put(question, response);
        assessmentRepository.save(assessment);
        logger.info("Updated assessment {} with response: {} -> {}", assessmentId, question, response);
    }

    public String getFinalDiagnosis(String assessmentId) {
        logger.info("Calculating final diagnosis for assessmentId: {}", assessmentId);
        Assessment assessment = assessmentRepository.findAssessmentByAssessmentId(assessmentId)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));

        Map<String, Integer> conditionCounts = new HashMap<>();
        for (String symptom : assessment.getSymptoms()) {
            Optional<Symptom> symptomData = symptomRepository.findByName(symptom);
            if (symptomData.isPresent()) {
                for (String condition : symptomData.get().getConditions()) {
                    conditionCounts.put(condition, conditionCounts.getOrDefault(condition, 0) + 1);
                }
            }
        }

        String finalDiagnosis = conditionCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Unknown Condition");

        logger.info("Final diagnosis for assessmentId {}: {}", assessmentId, finalDiagnosis);
        return finalDiagnosis;
    }
}
