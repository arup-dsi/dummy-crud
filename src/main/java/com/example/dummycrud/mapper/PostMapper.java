package com.example.dummycrud.mapper;

import com.example.dummycrud.entity.Post;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

public class PostMapper {

//    @DynamoDBHashKey(attributeName = "PostId")
//    @DynamoDBRangeKey(attributeName = "PersonId")
//    @DynamoDBAttribute(attributeName = "Title")
//    @DynamoDBAttribute(attributeName = "PostBody")
//    @DynamoDBAttribute(attributeName = "PostCreationTime")
//    @DynamoDBAttribute(attributeName = "Comments")

    public static Post fromMap(Map<String, AttributeValue> attributeValueMap) {
        Post post = new Post();
        post.setPostId(attributeValueMap.get("PostId").s());
        post.setPostContent(attributeValueMap.get("PostBody").s());
        post.setTitle(attributeValueMap.get("Title").s());
        post.setCreationTimeStamp(attributeValueMap.get("PostCreationTimeStamp").s());
        post.setPersonId(attributeValueMap.get("PersonId").s());
        // now how to set List?
   ///     List<Comment> comments = attributeValueMap.get("Comments").;
        return post;
    }


    public static Map<String, AttributeValue> toMap(Post post) {
        return Map.of(
                "PostId", AttributeValue.builder().s(post.getPostId()).build(),
                "PersonId", AttributeValue.builder().s(post.getPersonId()).build(),
                "Title",  AttributeValue.builder().s(post.getTitle()).build(),
                "PostBody",  AttributeValue.builder().s(post.getPostContent()).build(),
                "PostCreationTimeStamp",  AttributeValue.builder().s(post.getCreationTimeStamp()).build()
//                "Comments",  AttributeValue.builder().l(
//                        CommentMapper.convertCommentsToAttributeValues(post.getComments())
//                        ).build()
                );
    }
    // Since comment itself is an Attribute, I need to convert it and then put in here ~
}
