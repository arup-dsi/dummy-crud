package com.example.dummycrud.entity;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.Instant;

@DynamoDbBean
@DynamoDBTable(tableName = "Person")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Person {
    /*
    {
        "name": "AI",
    }
     */
    @DynamoDBHashKey
    @DynamoDBAttribute(attributeName = "Id")
    private String personId;
    @DynamoDBAttribute(attributeName = "Name")
    @NotNull
    private String name;
    @DynamoDBAttribute(attributeName = "CreationTimeStamp")
    private String creationTimeStamp;
}


