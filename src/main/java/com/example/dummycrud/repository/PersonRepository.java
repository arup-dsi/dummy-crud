package com.example.dummycrud.repository;

import com.amazonaws.services.kms.model.NotFoundException;
import com.example.dummycrud.entity.Person;
import com.example.dummycrud.exception.CustomException;
import com.example.dummycrud.mapper.PersonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.PagePublisher;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Repository
@Slf4j
public class PersonRepository {
   // private final DynamoDbEnhancedAsyncClient enhancedAsyncClient;
    private final DynamoDbAsyncClient asyncClient;
   // private final DynamoDbAsyncTable<Person> personDynamoDbAsyncTable;

    public PersonRepository(DynamoDbAsyncClient asyncClient) {
        this.asyncClient = asyncClient;
      //  this.personDynamoDbAsyncTable = enhancedAsyncClient.table(Person.class.getSimpleName(), TableSchema.fromBean(Person.class));
      //  System.out.println(personDynamoDbAsyncTable.tableName());
    }

    public CompletableFuture<Person> savePerson(Person person) {
        log.info("PersonRepository :: Inside save method");
        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName("Person")
                .item(PersonMapper.toMap(person))
                .build();
      //  return personDynamoDbAsyncTable.putItem(person);
        System.out.println(putItemRequest.toString());
        return asyncClient.putItem(putItemRequest)
                .thenApply(response -> {
                    if (response.sdkHttpResponse().isSuccessful()) {
                        return person;
                    } else {
                        throw new CustomException("Failed to save the Person object in DynamoDB");
                      //  throw new RuntimeException("Failed to save the Person object in DynamoDB");
                    }
                });
    }

    public CompletableFuture<Person> getPersonByID(String personId) {
        log.info("PersonRepository :: Inside getPersonByID method");
        //return personDynamoDbAsyncTable.getItem(getKeyBuild(personId));
        System.out.println(personId.length());
        GetItemRequest getItemRequest = GetItemRequest.builder()
                .tableName("Person")
                .key(Map.of("Id", AttributeValue.builder().s(personId).build()))
                .build();


        return asyncClient.getItem(getItemRequest)
                .thenApply(response -> {
                    if (response.hasItem()) {
                        log.info("Person object :: Response not empty");
                        return PersonMapper.fromMap(response.item());
                    } else {
                        log.info("Person object :: null");
                        throw new RuntimeException("Error finding person");
                        // not throwing CustomException because this method is also used by PostService
                    }
                });
    }


    public CompletableFuture<Person> updatePerson(Person person) {
        log.info("PersonRepository :: Inside updatePerson method");
        System.out.println(person.toString());
        log.info("PersonRepository :: Inside update method");
        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName("Person")
                .item(PersonMapper.toMap(person))
                .build();
        // Should've used updateItem for updating
        System.out.println(putItemRequest.toString());
        return asyncClient.putItem(putItemRequest)
                .thenApply(response -> {
                    if (response.sdkHttpResponse().isSuccessful()) {
                        return person;
                    } else {
                        throw new CustomException("Exception occured while updating Person");
                    }
                });
    }

//    public CompletableFuture<Person> deletePersonById(String personId) {
//        log.info("PersonRepository :: Inside deletePersonById method");
//      //  return personDynamoDbAsyncTable.deleteItem(getKeyBuild(personId));
//        return null;
//    }

    public CompletableFuture<Void> deletePersonById(String personId) {
        log.info("deletePersonById in repository ::: " + personId);
        DeleteItemRequest deleteItemRequest = DeleteItemRequest.builder()
                .tableName("Person")
                .key(Map.of("Id", AttributeValue.builder().s(personId).build()))
                .build();

        return asyncClient.deleteItem(deleteItemRequest)
                .thenApply(deleteItemResponse -> {
                    System.out.println(deleteItemResponse.toString());
                    return deleteItemResponse.sdkHttpResponse();
                })
                .thenAccept(response -> {
                    if (!response.isSuccessful()) {
                        log.info("Throwing RuntimeException");
                        throw new RuntimeException();
                    }
                });
    }

    // fetch all
  ///  public PagePublisher<Person> getAllPerson() {
//    public Flux<Person> getAllPerson() {
//        log.info("PersonRepository :: Inside getAllPerson method");
//      //  return personDynamoDbAsyncTable.scan();
//        ScanRequest scanRequest = ScanRequest.builder()
//                .tableName("Person")
//                .build();
//        return Flux.defer(() -> asyncClient.scan(scanRequest)
//               .thenApply(scanResponse -> {
//                   if(scanResponse.hasItems()) {
//                       return Flux.fromIterable(scanResponse.items())
//                               .map(stringAttributeValueMap -> Person.builder()
//                                       .name(stringAttributeValueMap.get("Id").toString())
//                                       .personId(stringAttributeValueMap.get("Name").toString())
//                                       .creationTimeStamp(stringAttributeValueMap.get("CreationTimeStamp").toString())
//                                       .build())
//                               .defaultIfEmpty(new Person());
//                   }
//               })
//        );
//    }


    public Flux<Person> getAllPerson() {
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName("Person")
                .build();

        return Flux.defer(() ->  asyncClient.scanPaginator(scanRequest))
                .flatMapIterable(ScanResponse::items)
                .map(item -> Person.builder()
                        .name(item.get("Name").toString())
                        .personId(item.get("Id").toString())
                        .creationTimeStamp(item.get("CreationTimeStamp").toString())
                        .build());
    }

    private Key getKeyBuild(String personId) {
        return Key.builder().partitionValue(personId).build();
    }

}
