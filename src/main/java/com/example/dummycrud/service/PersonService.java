package com.example.dummycrud.service;

import com.amazonaws.services.dynamodbv2.xspec.M;
import com.amazonaws.services.kms.model.NotFoundException;
import com.example.dummycrud.entity.Person;
import com.example.dummycrud.exception.CustomException;
import com.example.dummycrud.repository.PersonRepository;
import com.example.dummycrud.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.crypto.CipherSpi;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import java.util.function.LongSupplier;

import static com.example.dummycrud.util.Result.FAIL;
import static com.example.dummycrud.util.Result.SUCCESS;

@Service
@Slf4j
public class PersonService {

    private final PersonRepository personRepository;
    private final LongSupplier getEpochSecond = () -> Instant.now()
            .getEpochSecond();

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Mono<Result> createPerson(Person person) {
        log.info("updating creationTimeStamp and personId");
        person.setCreationTimeStamp(String.valueOf(getEpochSecond.getAsLong()));
        person.setPersonId(UUID.randomUUID().toString());
        log.info(person.toString());
        return Mono.fromFuture(personRepository.savePerson(person))
                .thenReturn(SUCCESS)
                .onErrorReturn(FAIL); // no need since exception is already handled there
    }
    public Mono<Person> getPersonByPersonId(String personId) {
        // Can't I just directly return  Mono.fromFuture(personRepository.getPersonByID(personId))?
        return Mono.fromFuture(personRepository.getPersonByID(personId));

//                .flatMap(person -> {
//                    if (person != null) {
//                        return Mono.just(person);
//                    } else {
//                        return Mono.empty();
//                    }
//                })
                //.onErrorMap(throwable -> new CustomException("Trouble fetching Person with personId: " + personId));
    }

    public Mono<Result> deletePersonById(String personId) {
        log.info("deletePersonById in Service");
        return Mono.fromFuture(personRepository.deletePersonById(personId))
                .thenReturn(SUCCESS)
                .onErrorReturn(FAIL); // handles thrown exceptipn ~ exception thrown
    }

    // check the update method again
    public Mono<Person> updatePerson(Person person) {
        log.info(person.toString());
        person.setCreationTimeStamp(String.valueOf(getEpochSecond.getAsLong()));

        return Mono.fromFuture(personRepository.getPersonByID(person.getPersonId()))
                .filter(Objects::nonNull) // filter will send empty Mono if it doesn't find anything
                .flatMap(previousPersonValue ->
                        Mono.fromFuture(personRepository.updatePerson(person)));
                //.onErrorMap(throwable -> new CustomException("Exception while updating"));
    }
    public Flux<Person> getPersonList() {
        return personRepository.getAllPerson();
    }
}
