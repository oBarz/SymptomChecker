package com.symptomchecke.symptom_checker.repository;

import com.symptomchecke.symptom_checker.entity.Symptom;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@EnableScan
public interface SymptomRepository extends CrudRepository<Symptom, String> {
    @EnableScan
    Optional<Symptom> findByName(String name);
}
