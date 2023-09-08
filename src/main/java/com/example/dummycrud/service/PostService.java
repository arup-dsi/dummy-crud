package com.example.dummycrud.service;

import com.example.dummycrud.entity.Person;
import com.example.dummycrud.entity.Post;
import com.example.dummycrud.exception.CustomException;
import com.example.dummycrud.repository.PersonRepository;
import com.example.dummycrud.repository.PostRepository;
import com.example.dummycrud.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import java.util.function.LongSupplier;

import static com.example.dummycrud.util.Result.FAIL;
import static com.example.dummycrud.util.Result.SUCCESS;

@Service
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final PersonRepository personRepository;
    private final LongSupplier getEpochSecond = () -> Instant.now()
            .getEpochSecond();

    public PostService(PostRepository postRepository,  PersonRepository personRepository) {
        this.postRepository = postRepository;
        this.personRepository = personRepository;
    }

    //Just return the post
    public Mono<Post> createPost(Post post, String personId) {
        log.info("PostService :: createPost");
        return Mono.fromFuture(personRepository.getPersonByID(personId))
                .filter(Objects::nonNull) // will emit empty Mono if person doesn't pass filter
                .flatMap(person -> {
                        post.setPersonId(personId);
                        post.setCreationTimeStamp(String.valueOf(getEpochSecond.getAsLong()));
                        post.setPostId(UUID.randomUUID().toString());
                        log.info("FINAL Post object :: " + post);
                        return Mono.fromFuture(postRepository.savePost(post));
                })
                .onErrorMap(throwable -> new CustomException("Person not found with personId: " + personId + " for creating Post"));/// return Mono.fromFuture(postRepository.savePost(post));
    }


    public Mono<Post> getPostByPostId(String postId) {
        log.info("PostService :: getPostByPostId");
        return Mono.fromFuture(postRepository.getPostById(postId))
                .filter(Objects::nonNull)
                .flatMap(Mono::just)
                .onErrorMap(throwable -> new CustomException("Post not found"));
    }

    public Mono<Result> deletePostByPostId(String postId) {
        log.info("deletePostByPostId in Service");
        return Mono.fromFuture(postRepository.deletePostById(postId))
                .thenReturn(SUCCESS)
                .onErrorReturn(FAIL); //Exception handled here ~
    }

    public Flux<Post> findPostByPersonId(String personId) {
        log.info("PostService :: findPostByPersonId");
        return postRepository.findPostsByPersonId(personId)
                .onErrorMap(throwable -> new CustomException("Unable to fetch Post's with personId: " + personId));
        // no exception is thrown in repository ~
    }

}
