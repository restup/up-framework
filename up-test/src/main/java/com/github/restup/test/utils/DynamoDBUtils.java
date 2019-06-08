package com.github.restup.test.utils;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;

public class DynamoDBUtils {

    /**
     * Init connection to local dynamo or embedded depending upon presence of
     * sqlite4java.library.path
     */
    public static AmazonDynamoDB init() {
        AmazonDynamoDB ddb;
        // see https://www.javacodegeeks.com/2019/01/testing-dynamodb-using-junit5.html
        // also https://github.com/aws-samples/aws-dynamodb-examples/blob/master/pom.xml
        // with https://github.com/aws-samples/aws-dynamodb-examples/blob/master/src/test/java/com/amazonaws/services/dynamodbv2/local/embedded/DynamoDBEmbeddedTest.java
        if (null != System.getProperty("sqlite4java.library.path")) {
            ddb = DynamoDBEmbedded.create().amazonDynamoDB();
        } else {
            //XXX might be a nicer way of doing this, but this works in ide vs mvn build
            ddb = AmazonDynamoDBClientBuilder.standard().withCredentials(
                new AWSStaticCredentialsProvider(new BasicAWSCredentials("local", "null"))).
                withEndpointConfiguration(new EndpointConfiguration("http://localhost:8000", ""))
                .build();
        }
        return ddb;
    }

    public static DynamoDBMapper createTables(AmazonDynamoDB ddb,
        Class<?>... classes) {
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);
        createTables(ddb, mapper, classes);
        return mapper;
    }

    public static void createTables(AmazonDynamoDB ddb, DynamoDBMapper mapper,
        Class<?>... classes) {
        for (Class c : classes) {
            CreateTableRequest tableRequest = mapper.generateCreateTableRequest(c)
                .withProvisionedThroughput(
                    new ProvisionedThroughput()
                        .withReadCapacityUnits(1l)
                        .withWriteCapacityUnits(1l));
            ddb.createTable(tableRequest);
        }
    }

}
