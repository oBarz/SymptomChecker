package com.symptomchecke.symptom_checker.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@DynamoDBTable(tableName = "Symptom")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Symptom {

    @DynamoDBHashKey(attributeName = "name")
    private String name;

    @DynamoDBAttribute(attributeName = "questions")
    private List<String> questions; // Dynamic questions for this symptom

    @DynamoDBAttribute(attributeName = "conditions")
    private List<String> conditions; // Possible conditions associated with this symptom
}
