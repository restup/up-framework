package com.stack;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import java.time.ZonedDateTime;

@DynamoDBTable(tableName = "StackReleaseEvent")
public class StackReleaseEvent extends AbstractRelease {

    // stage-101-jobservice
    @DynamoDBHashKey(attributeName = "StackComponent")
    private String stackComponent;
    @DynamoDBRangeKey(attributeName = "CreateDateTime")
    private ZonedDateTime created;

    @DynamoDBIndexHashKey(attributeName = "Stack")
    private String stack;
    @DynamoDBIndexRangeKey(attributeName = "Component")
    private String component;
}
