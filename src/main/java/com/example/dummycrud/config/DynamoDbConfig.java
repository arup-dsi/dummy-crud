package com.example.dummycrud.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

import java.net.URI;

// http://localhost:8001/      DYNAMODB
// http://localhost:4566/      LOCALSTACK

@Configuration
@Slf4j
public class DynamoDbConfig {
    private final String dynamoDbEndPointUrl;
    private final String accessKey;
    private final String secretKey;

    public DynamoDbConfig(@Value("${application.dynamodb.endpoint}") String dynamoDbEndPointUrl,
                    @Value("${application.dynamodb.accessKey}") String accessKey,
                    @Value("${application.dynamodb.secretKey}") String secretKey) {
        this.dynamoDbEndPointUrl = dynamoDbEndPointUrl;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }


    @Bean
    public DynamoDbAsyncClient getDynamoDbAsyncClient() {
        return DynamoDbAsyncClient.builder()
                .credentialsProvider(() -> new AwsCredentials() {
                    @Override
                    public String accessKeyId() {
                        return accessKey;
                    }
                    @Override
                    public String secretAccessKey() {
                        return secretKey;
                    }
                })
                .region(Region.AP_SOUTHEAST_1)
                .endpointOverride(URI.create(dynamoDbEndPointUrl))
                .build();
    }

    @Bean
    public DynamoDbEnhancedAsyncClient getDynamoDbEnhancedAsyncClient() {
        log.info("DynamoDbEnhancedAsyncClient ready");
        return DynamoDbEnhancedAsyncClient.builder()
                .dynamoDbClient(getDynamoDbAsyncClient())
                .build();
    }
}