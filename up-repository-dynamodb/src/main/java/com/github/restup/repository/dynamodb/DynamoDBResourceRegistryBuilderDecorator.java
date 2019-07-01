package com.github.restup.repository.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.github.restup.mapping.MappedClassBuilderDecorator;
import com.github.restup.mapping.fields.MappedFieldBuilderDecorator;
import com.github.restup.query.Pagination;
import com.github.restup.registry.ResourceRegistry.Builder;
import com.github.restup.registry.ResourceRegistryBuilderDecorator;
import com.github.restup.repository.dynamodb.mapping.DynamoDBClassBuilderDecorator;
import com.github.restup.repository.dynamodb.mapping.DynamoDBFieldBuilderDecorator;

public class DynamoDBResourceRegistryBuilderDecorator implements ResourceRegistryBuilderDecorator {

    public final AmazonDynamoDB amazonDynamoDB;

    public DynamoDBResourceRegistryBuilderDecorator(
        AmazonDynamoDB ddb) {
        amazonDynamoDB = ddb;
    }

    public DynamoDBResourceRegistryBuilderDecorator() {
        this(null);
    }

    @Override
    public Builder decorate(Builder builder) {

        //TODO read optional properties for local settings
        String amazonDynamoDBEndpoint = null;
        String amazonDynamoDBRegion = null;
        DynamoDBRepositoryFactory dynamoDBRepositoryFactory = DynamoDBRepositoryFactory
            .builder()
            .amazonDynamoDB(amazonDynamoDB)
            .amazonDynamoDB(amazonDynamoDBEndpoint, amazonDynamoDBRegion)
            .build();

        return builder
            .repositoryFactory(dynamoDBRepositoryFactory)
            .mappedFieldBuilderDecoratorBuilder(
                MappedFieldBuilderDecorator.builder().add(new DynamoDBFieldBuilderDecorator())
            )
            .mappedClassBuilderDecoratorBuilder(
                MappedClassBuilderDecorator.builder().add(new DynamoDBClassBuilderDecorator()))
            .defaultPagination(Pagination.of(100, 10, null));
    }

}
