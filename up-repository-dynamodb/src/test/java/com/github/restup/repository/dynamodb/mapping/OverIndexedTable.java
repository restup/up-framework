package com.github.restup.repository.dynamodb.mapping;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.github.restup.annotations.ApiName;

@DynamoDBTable(tableName = "OverIndexedTable")
@ApiName("oit")
public class OverIndexedTable {

    @DynamoDBHashKey(attributeName = "id")
    private String id;
    @DynamoDBIndexHashKey(attributeName = "foo", globalSecondaryIndexNames = {"idx1", "idx3"})
    @DynamoDBIndexRangeKey(attributeName = "foo", globalSecondaryIndexNames = {"idx2", "idx4"})
    private String foo;
    @DynamoDBIndexHashKey(attributeName = "bar", globalSecondaryIndexName = "idx2")
    @DynamoDBIndexRangeKey(attributeName = "bar", globalSecondaryIndexNames = {"idx1"})
    private String bar;
    @DynamoDBIndexHashKey(attributeName = "baz", globalSecondaryIndexName = "idx4")
    @DynamoDBIndexRangeKey(attributeName = "baz", globalSecondaryIndexNames = {"idx3"})
    private String baz;
    @DynamoDBAttribute(attributeName = "fooBar")
    private String foobar;
    @DynamoDBIgnore
    private String ignoreme;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFoo() {
        return foo;
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }

    public String getBar() {
        return bar;
    }

    public void setBar(String bar) {
        this.bar = bar;
    }

    public String getBaz() {
        return baz;
    }

    public void setBaz(String baz) {
        this.baz = baz;
    }

    public String getFoobar() {
        return foobar;
    }

    public void setFoobar(String foobar) {
        this.foobar = foobar;
    }

    public String getIgnoreme() {
        return ignoreme;
    }

    public void setIgnoreme(String ignoreme) {
        this.ignoreme = ignoreme;
    }
}
