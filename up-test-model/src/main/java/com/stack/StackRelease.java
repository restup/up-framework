package com.stack;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import java.time.ZonedDateTime;

@DynamoDBTable(tableName = "StackRelease")
public class StackRelease extends AbstractRelease {

    @DynamoDBHashKey(attributeName = "Stack")
    private String stack;
    @DynamoDBRangeKey(attributeName = "Component")
    @DynamoDBIndexHashKey(attributeName = "Component", globalSecondaryIndexName = "Component-index")
    private String component;
    @DynamoDBAttribute(attributeName = "UpdateDateTime")
    private ZonedDateTime updated;
    @DynamoDBAttribute(attributeName = "LatestReleaseDateTime")
    private ZonedDateTime versionChanged;

    public String getStack() {
        return stack;
    }

    public void setStack(String stack) {
        this.stack = stack;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public ZonedDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(ZonedDateTime updated) {
        this.updated = updated;
    }

    public ZonedDateTime getVersionChanged() {
        return versionChanged;
    }

    public void setVersionChanged(ZonedDateTime versionChanged) {
        this.versionChanged = versionChanged;
    }
}
