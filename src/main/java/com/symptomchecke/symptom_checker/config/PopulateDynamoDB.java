package com.symptomchecke.symptom_checker.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

public class PopulateDynamoDB {

    private static final String DYNAMO_DB_ENDPOINT = "http://localhost:8000"; // Change if needed
    private static final String AWS_REGION = "eu-west-2";
    private static final String AWS_ACCESS_KEY = "dummy";
    private static final String AWS_SECRET_KEY = "dummy";

    private static final Map<String, String> TABLES = Map.of(
            "Casa", "name",
            "User", "email",
            "Symptom", "name",
            "Assessment", "assessmentId"
    );

    public static void main(String[] args) {
        AmazonDynamoDB client = createDynamoDBClient();
        DynamoDB dynamoDB = new DynamoDB(client);

        System.out.println("⏳ Waiting for DynamoDB Local...");
        waitForDynamoDB(client);

        // ✅ Step 1: Create missing tables
        for (Map.Entry<String, String> entry : TABLES.entrySet()) {
            createTableIfNotExists(dynamoDB, client, entry.getKey(), entry.getValue());
        }

        // ✅ Step 2: Populate only empty tables
        for (String tableName : TABLES.keySet()) {
            populateTableIfEmpty(dynamoDB, client, tableName);
        }

        System.out.println("✅ DynamoDB setup completed successfully!");
    }

    private static AmazonDynamoDB createDynamoDBClient() {
        return AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(DYNAMO_DB_ENDPOINT, AWS_REGION))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY)))
                .build();
    }

    private static void waitForDynamoDB(AmazonDynamoDB client) {
        int retries = 10;
        int waitTimeMs = 5000;

        for (int i = 0; i < retries; i++) {
            try {
                List<String> tableNames = client.listTables().getTableNames();
                if (tableNames != null) {
                    System.out.println("✅ DynamoDB is ready: " + tableNames);
                    return;
                }
            } catch (Exception e) {
                System.out.println("⏳ Waiting for DynamoDB...");
            }
            try {
                Thread.sleep(waitTimeMs);
            } catch (InterruptedException ignored) {}
        }
        System.err.println("❌ ERROR: DynamoDB Local did not become available!");
    }

    private static void createTableIfNotExists(DynamoDB dynamoDB, AmazonDynamoDB client, String tableName, String primaryKey) {
        if (client.listTables().getTableNames().contains(tableName)) {
            System.out.println("✔ Table " + tableName + " already exists.");
            return;
        }

        try {
            System.out.println("🔹 Creating table: " + tableName);
            Table table = dynamoDB.createTable(
                    tableName,
                    Arrays.asList(new KeySchemaElement(primaryKey, KeyType.HASH)),
                    Arrays.asList(new AttributeDefinition(primaryKey, ScalarAttributeType.S)),
                    new ProvisionedThroughput(5L, 5L)
            );
            table.waitForActive();
            System.out.println("✅ Table created: " + tableName);
        } catch (Exception e) {
            System.err.println("❌ ERROR creating table " + tableName + ": " + e.getMessage());
        }
    }

    private static void populateTableIfEmpty(DynamoDB dynamoDB, AmazonDynamoDB client, String tableName) {
        try {
            Table table = dynamoDB.getTable(tableName);
            long itemCount = table.describe().getItemCount();

            if (itemCount > 0) {
                System.out.println("✔ Table " + tableName + " already contains data. Skipping population.");
                return;
            }

            // Load JSON file
            String jsonFilePath = "DB/" + tableName + ".json";
            InputStream inputStream = PopulateDynamoDB.class.getClassLoader().getResourceAsStream(jsonFilePath);

            if (inputStream == null) {
                System.err.println("❌ JSON not found: " + jsonFilePath);
                return;
            }

            System.out.println("🔹 Populating empty table: " + tableName + " from " + jsonFilePath);

            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, Object>> items = objectMapper.readValue(inputStream, new TypeReference<List<Map<String, Object>>>() {});

            for (Map<String, Object> item : items) {
                Item dynamoDBItem = new Item();

                for (Map.Entry<String, Object> entry : item.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();

                    // Convert lists properly
                    if (value instanceof List<?>) {
                        List<?> list = (List<?>) value;
                        List<String> stringList = new ArrayList<>();
                        for (Object obj : list) {
                            stringList.add(obj.toString()); // Convert each item to String
                        }
                        dynamoDBItem.withList(key, stringList);
                    }
                    // Convert maps properly
                    else if (value instanceof Map<?, ?>) {
                        Map<?, ?> map = (Map<?, ?>) value;
                        Map<String, String> stringMap = new HashMap<>();
                        for (Map.Entry<?, ?> mapEntry : map.entrySet()) {
                            stringMap.put(mapEntry.getKey().toString(), mapEntry.getValue().toString());
                        }
                        dynamoDBItem.withMap(key, stringMap);
                    }
                    // Convert all other fields
                    else {
                        dynamoDBItem.withString(key, value.toString());
                    }
                }

                // Insert the item into DynamoDB
                table.putItem(dynamoDBItem);
            }

            System.out.println("✅ Table " + tableName + " populated.");
        } catch (Exception e) {
            System.err.println("❌ ERROR populating table " + tableName + ": " + e.getMessage());
        }
    }


}
