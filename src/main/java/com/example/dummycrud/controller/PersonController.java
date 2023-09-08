package com.example.dummycrud.controller;

import com.amazonaws.services.dynamodbv2.xspec.M;
import com.example.dummycrud.entity.Person;
import com.example.dummycrud.exception.CustomException;
import com.example.dummycrud.service.PersonService;
import com.example.dummycrud.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Objects;

@RestController
@Slf4j
public class PersonController {
    private PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping("/create")
    public Mono<ResponseEntity<Result>> createPerson(@RequestBody Person person) {
        log.info("PersonController :: inside createPerson method");
        log.info(person.toString());
        return personService.createPerson(person)
                .map(result -> {
                    if (result.equals(Result.SUCCESS)) {
                        return ResponseEntity.status(HttpStatus.CREATED).body(result);
                    } else {
                        // when exception occurs, it returns FAILED
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
                    }
                }); /// onErrorReturn handles exception. So no exception will reach here.
             //   .onErrorMap(throwable -> new CustomException("Exception thrown while creating Person"));
    }

    @GetMapping("/get/{personId}")
    public Mono<ResponseEntity<Person>> getPersonById(@PathVariable String personId) {
        return personService.getPersonByPersonId(personId)
                .map(ResponseEntity::ok) // Return OK response with the Person object
                .defaultIfEmpty(ResponseEntity.notFound().build());
//                .onErrorMap(throwable -> new CustomException(throwable.getMessage())); // Return NOT FOUND response if no Person is found
    }

    @DeleteMapping("/delete/{personId}")
    public Mono<ResponseEntity<String>> deletePersonById(@PathVariable String personId) {
        log.info("DELETE method in service layer");
        return personService.deletePersonById(personId)
                .map(result -> {
                    if (result == Result.SUCCESS) {
                        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Person deleted having Id: " + personId); // Successful deletion
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Person not found for deletion"); // return 404 (NOT_FOUND)
                    }
                });
               // .onErrorMap(throwable -> new CustomException("Exception occurred while trying to delete Person with personId: " + personId));
    }

//    @GetMapping("/allCustomerList")
//    public Flux<Person> getPersonList() {
//        return personService.getPersonList();
//    }

    @PutMapping("/update")
    public Mono<ResponseEntity<?>> updatePerson(@RequestBody Person person) {
        log.info("PersonController :: inside createPerson method");
        System.out.println(person.toString());
        return personService.updatePerson(person)
                .map(updatedPerson -> Objects.nonNull(updatedPerson) ?
                        ResponseEntity.ok().body(updatedPerson) : ResponseEntity.noContent().build());
//                .map(result -> {
//                    if (result == Result.SUCCESS) {
//                        return ResponseEntity.status(HttpStatus.OK).body(result);
//                    } else {
//                        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result);
//                    }
//                });
    }


    // WORKING bleh
    @GetMapping(value = "/find/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<Person> findAll() {
        return personService.getPersonList()
                .switchIfEmpty(Flux.empty());
    }

    @GetMapping("/get/all")
    public ResponseEntity<Flux<Person>> getAllPersons() {
        var persons = personService.getPersonList();
        return new ResponseEntity<>(persons, HttpStatus.OK);
    }
}
