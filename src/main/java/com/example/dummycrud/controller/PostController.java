package com.example.dummycrud.controller;

import com.amazonaws.services.dynamodbv2.xspec.S;
import com.example.dummycrud.entity.Person;
import com.example.dummycrud.entity.Post;
import com.example.dummycrud.exception.CustomException;
import com.example.dummycrud.service.PostService;
import com.example.dummycrud.util.Result;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class PostController {

    private PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/post/create/{personId}")
    public Mono<ResponseEntity<Post>> createPost(@RequestBody @Valid Post post, @PathVariable("personId") String personId) {
        log.info("PostController :: inside createPost method");
        return postService.createPost(post, personId)
                .map(postValue -> {
                    System.out.println(postValue);
                   return new ResponseEntity<>(postValue, HttpStatus.CREATED);
                })
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @GetMapping("/post/get/{postId}")
    public Mono<ResponseEntity<Post>> getPostByPostId(@PathVariable String postId) {
        log.info("postId :: " + postId);
        log.info("postId length :: " + postId.length());
        return postService.getPostByPostId(postId)
                .map(ResponseEntity::ok) // Return OK response with the Post object
                .defaultIfEmpty(ResponseEntity.notFound().build()); // Return NOT FOUND response if no Post is found
    }

    @DeleteMapping("/post/delete/{postId}")
    public Mono<ResponseEntity<Void>> deletePostById(@PathVariable String postId) {
        log.info("DELETE method in service layer");
        return postService.deletePostByPostId(postId)
                .map(result -> {
                    if (result == Result.SUCCESS) {
                        return ResponseEntity.status(HttpStatus.OK).build(); // Successful deletion
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Post not found, return 404 (NOT_FOUND)
                    }
                });
    }

    @GetMapping("/posts/all/{personId}")
    public ResponseEntity<Flux<Post>> findPostsById(@PathVariable String personId) {
        Flux<Post> persons = postService.findPostByPersonId(personId);
        return new ResponseEntity<>(persons, HttpStatus.OK);
    }
}
