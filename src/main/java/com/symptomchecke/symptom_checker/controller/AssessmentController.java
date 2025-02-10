package com.symptomchecke.symptom_checker.controller;

import com.symptomchecke.symptom_checker.dto.AnswerRequest;
import com.symptomchecke.symptom_checker.dto.AssessmentRequest;
import com.symptomchecke.symptom_checker.entity.Assessment;
import com.symptomchecke.symptom_checker.service.AssessmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/assessment")
@Tag(name = "Assessment", description = "User Symptom Assessment API")
public class AssessmentController {

    private final AssessmentService assessmentService;

    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @Operation(summary = "Start a new assessment", description = "Creates a new symptom assessment for the user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Assessment started successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Assessment.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    @PostMapping("/start")
    public ResponseEntity<Assessment> startAssessment(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Email and initial symptoms")
            @RequestBody AssessmentRequest assessmentRequest) {
        String email = assessmentRequest.getEmail();
        List<String> initialSymptoms = assessmentRequest.getInitialSymptoms();
        return ResponseEntity.ok(assessmentService.startAssessment(email, initialSymptoms));
    }

    @Operation(summary = "Get next question", description = "Returns the next question based on symptoms")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Next question retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Assessment not found")
    })
    @GetMapping("/{assessmentId}/next-question")
    public ResponseEntity<String> getNextQuestion(
            @PathVariable String assessmentId) {
        return ResponseEntity.ok(assessmentService.getNextQuestion(assessmentId));
    }

    @Operation(summary = "Submit a response", description = "Saves the user's response to a question")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Response saved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Assessment not found")
    })
    @PutMapping("/{assessmentId}/answer")
    public ResponseEntity<Void> updateAssessment(
            @PathVariable String assessmentId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Question and response")
            @RequestBody AnswerRequest request) {
        assessmentService.updateAssessment(assessmentId, request.getQuestionId(), request.getResponse());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get final diagnosis", description = "Returns the probable condition based on symptoms")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Diagnosis retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Assessment not found")
    })
    @GetMapping("/{assessmentId}/diagnosis")
    public ResponseEntity<String> getFinalDiagnosis(
            @PathVariable String assessmentId) {
        return ResponseEntity.ok(assessmentService.getFinalDiagnosis(assessmentId));
    }
}
