package com.symptomchecke.symptom_checker.repository;

import com.symptomchecke.symptom_checker.entity.User;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@EnableScan
public interface UserRepository extends CrudRepository<User, String> {
    Optional<User> findByEmail(String email);
}
