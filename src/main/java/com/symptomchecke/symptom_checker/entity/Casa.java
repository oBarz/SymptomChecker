package com.symptomchecke.symptom_checker.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@DynamoDBTable(tableName = "Casa")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Casa {

    @DynamoDBHashKey(attributeName = "name")
    private String name;

    @DynamoDBAttribute(attributeName = "exterior")
    private List<String> exterior;

    @DynamoDBAttribute(attributeName = "interior")
    private List<String> interior;
}
