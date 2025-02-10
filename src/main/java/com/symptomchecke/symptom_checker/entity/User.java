package com.symptomchecke.symptom_checker.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@DynamoDBTable(tableName = "User")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @DynamoDBHashKey(attributeName = "email")
    private String email;

    @DynamoDBAttribute(attributeName = "password")
    private String password;

    @DynamoDBAttribute(attributeName = "age")
    private int age;

    @DynamoDBAttribute(attributeName = "gender")
    private String gender;

    @DynamoDBAttribute(attributeName = "reportedSymptoms")
    private List<String> reportedSymptoms;
}
