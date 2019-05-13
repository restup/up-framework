package com.stack;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import java.time.ZonedDateTime;
import java.util.Map;

@DynamoDBTable(tableName = "StackVersion")
public class StackVersion {

    @DynamoDBHashKey(attributeName = "Stack")
    private String stack;
    @DynamoDBRangeKey(attributeName = "VersionDateTime")
    private ZonedDateTime versionDateTime;

    @DynamoDBAttribute(attributeName = "StackVersionId")
    private String stackVersionId;

    @DynamoDBAttribute(attributeName = "Components")
    private Map<String, String> components;


}
