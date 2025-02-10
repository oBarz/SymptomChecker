package com.symptomchecke.symptom_checker.repository;

import com.symptomchecke.symptom_checker.entity.Assessment;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@EnableScan
@Repository
public interface AssessmentRepository extends CrudRepository<Assessment, String> {

///*    @Override
//    @EnableScan
//    Optional<Assessment> findById(String assessmentId);*/


/*    @EnableScan
    Optional<Assessment> getAssessmentById(String assessmentId);*/


    @EnableScan
    Optional<Assessment> findAssessmentByAssessmentId(String assessmentId);
}

