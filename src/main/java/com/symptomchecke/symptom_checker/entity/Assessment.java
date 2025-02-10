package com.symptomchecke.symptom_checker.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@DynamoDBTable(tableName = "Assessment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Assessment {

    @DynamoDBHashKey(attributeName = "assessmentId")
    //@DynamoDBAttribute(attributeName = "assessmentId")
    private String assessmentId;

    @DynamoDBAttribute(attributeName = "email")
    private String email;

    @DynamoDBAttribute(attributeName = "symptoms")
    private List<String> symptoms;

    @DynamoDBAttribute(attributeName = "responses")
    private Map<String, String> responses;
}
