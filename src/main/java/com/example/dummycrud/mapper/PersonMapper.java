package com.example.dummycrud.mapper;

import com.example.dummycrud.entity.Person;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

public class PersonMapper {
    public static Person fromMap(Map<String, AttributeValue> attributeValueMap) {
        Person person = new Person();
        person.setPersonId(attributeValueMap.get("Id").s());
        person.setName(attributeValueMap.get("Name").s());
        person.setCreationTimeStamp(attributeValueMap.get("CreationTimeStamp").s());
        return person;
    }


    public static Map<String, AttributeValue> toMap(Person person) {
        return Map.of(
                "Id", AttributeValue.builder().s(person.getPersonId()).build(),
                "Name", AttributeValue.builder().s(person.getName()).build(),
                "CreationTimeStamp",  AttributeValue.builder().s(person.getCreationTimeStamp()).build()
        );
    }
}
