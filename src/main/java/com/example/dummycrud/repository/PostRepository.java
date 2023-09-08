package com.example.dummycrud.repository;

import com.example.dummycrud.entity.Person;
import com.example.dummycrud.entity.Post;
import com.example.dummycrud.exception.CustomException;
import com.example.dummycrud.mapper.PersonMapper;
import com.example.dummycrud.mapper.PostMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Repository
@Slf4j
public class PostRepository {
    private final DynamoDbAsyncClient asyncClient;
    public PostRepository(DynamoDbAsyncClient asyncClient) {
        this.asyncClient = asyncClient;
    }
    public CompletableFuture<Post> savePost(Post post) {
        log.info("PostRepository :: Inside save method");
        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName("Post")
                .item(PostMapper.toMap(post))
                .build();
        return asyncClient.putItem(putItemRequest)
                .thenApply(response -> {
                    if (response.sdkHttpResponse().isSuccessful()) {
                        return post;
                    } else {
                        throw new CustomException("Failed to save Post");
                    }
                });
    }


    public CompletableFuture<Post> getPostById(String postId) {
        log.info("PostRepository :: Inside getPostById method");
        log.info("id length :: " + postId.length());
        //return personDynamoDbAsyncTable.getItem(getKeyBuild(personId));
        GetItemRequest getItemRequest = GetItemRequest.builder()
                .tableName("Post")
                .key(Map.of("PostId", AttributeValue.builder().s(postId).build()))
                .build();

        return asyncClient.getItem(getItemRequest)
                .thenApply(response -> {
                    if (response.hasItem()) {
                        return PostMapper.fromMap(response.item());
                    } else {
                        /// Should I throw Exception?
                        return null;
                    }
                });
    }


    // New implementation

    public Mono<Post> findById(String postId) {
        QueryRequest queryRequest = QueryRequest.builder()
                .tableName("Post")
                .keyConditionExpression("postId = :v_postId")
                .expressionAttributeValues(Collections.singletonMap(":v_postId", AttributeValue.builder().s(postId).build()))
                .build();

        return Mono.fromFuture(() -> asyncClient.query(queryRequest))
                .flatMap(queryResponse -> {
                    if (queryResponse.count() == 0) {
                        return Mono.empty(); // Post not found
                    } else {
                        // Assuming there is only one matching post, retrieve it from the result
                        Map<String, AttributeValue> item = queryResponse.items().get(0);
                        Post post = PostMapper.fromMap(item); // Implement ItemMapper
                        return Mono.just(post);
                    }
                });
    }


    public CompletableFuture<Post> updatePost(Post post) {
        log.info("PostRepository :: Inside updatePost method");
        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName("Post")
                .item(PostMapper.toMap(post))
                .build();
        return asyncClient.putItem(putItemRequest)
                .thenApply(response -> {
                    if (response.sdkHttpResponse().isSuccessful()) {
                        log.info("Updated post :: " + post);
                        return post;
                    } else {
                        throw new RuntimeException("Failed to save the Post object in DynamoDB");
                    }
                });
    }


    public CompletableFuture<Void> deletePostById(String postId) {
        log.info("deletePostById in repository ::: " + postId);
        DeleteItemRequest deleteItemRequest = DeleteItemRequest.builder()
                .tableName("Post")
                .key(Map.of("PostId", AttributeValue.builder().s(postId).build()))
                .build();

        return asyncClient.deleteItem(deleteItemRequest)
                .thenApply(deleteItemResponse -> {
                    System.out.println(deleteItemResponse.toString());
                    return deleteItemResponse.sdkHttpResponse();
                })
                .thenAccept(response -> {
                    if (!response.isSuccessful()) {
                        throw new RuntimeException("Failed to delete the Post object");
                    }
                    // if successful, do nothing
                });
    }




//    public Flux<Post> findPostsByPersonId(String personId) {
//        QueryRequest queryRequest = QueryRequest.builder()
//                .tableName("Post")
//                .keyConditionExpression("personId = :v_personId")
//                .expressionAttributeValues(Collections.singletonMap(":v_personId", AttributeValue.builder().s(personId).build()))
//                .build();
//
//        return Mono.fromFuture(() -> asyncClient.query(queryRequest))
//                .flatMapMany(queryResponse -> {
//                    return Flux.fromIterable(queryResponse.items())
//                            .map(PostMapper::fromMap);
//                });
//    }

    public Flux<Post> findPostsByPersonId(String personId) {
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName("Post")
                .build();

        // How can I solve this without scanning?
        // LSI?
        // postId is already a partition key
        // so by declaring personId as sort key, maybe afterwards I'll be able to
        // fetch all Post's with personId?

        return Flux.defer(() ->  asyncClient.scanPaginator(scanRequest))
                .flatMapIterable(ScanResponse::items)
                .filter(item -> item.get("PersonId").s().equals(personId))
                .map(PostMapper::fromMap);
    }


    private Key getKeyBuild(String postId) {
        return Key.builder().partitionValue(postId).build();
    }
}
