package com.example.dummycrud.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.time.Instant;
import java.util.List;

//@DynamoDbBean     to declare it as a table
@DynamoDbBean
@DynamoDBTable(tableName = "Post")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Post {
    // rangeKey + hashKey -> this combination should be unique
    @DynamoDBHashKey
    @DynamoDBAttribute(attributeName = "PostId")
    private String postId;
    @DynamoDBAttribute(attributeName = "PersonId")
    private String personId;
    @DynamoDBAttribute(attributeName = "Title")
    private String title;
    @NotNull
    @DynamoDBAttribute(attributeName = "PostBody")
    private String postContent;
    @DynamoDBAttribute(attributeName = "PostCreationTimeStamp")
    private String creationTimeStamp;
}
