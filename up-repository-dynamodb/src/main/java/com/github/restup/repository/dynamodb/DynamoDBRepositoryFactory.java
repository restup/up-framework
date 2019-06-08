package com.github.restup.repository.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.github.restup.mapping.MappedClassBuilderDecorator;
import com.github.restup.mapping.MappedClassBuilderDecoratorSupplier;
import com.github.restup.mapping.fields.MappedFieldBuilderDecorator;
import com.github.restup.mapping.fields.MappedFieldBuilderDecoratorSupplier;
import com.github.restup.query.DefaultPaginationSupplier;
import com.github.restup.query.Pagination;
import com.github.restup.registry.Resource;
import com.github.restup.repository.RepositoryFactory;
import com.github.restup.repository.dynamodb.mapping.DynamoDBClassBuilderDecorator;
import com.github.restup.repository.dynamodb.mapping.DynamoDBFieldBuilderDecorator;
import java.util.Arrays;
import java.util.List;

public class DynamoDBRepositoryFactory implements RepositoryFactory,
    MappedFieldBuilderDecoratorSupplier, MappedClassBuilderDecoratorSupplier,
    DefaultPaginationSupplier {

    private final DynamoDBRepository<?, ?> repository;
    private final DynamoDBFieldBuilderDecorator dynamoDBFieldBuilderDecorator;
    private final DynamoDBClassBuilderDecorator dynamoDBClassBuilderDecorator;
    private final Pagination defaultPagination;

    public DynamoDBRepositoryFactory(DynamoDBMapper mapper) {
        this(mapper, Pagination.of(100, 10, null));
    }

    public DynamoDBRepositoryFactory(DynamoDBMapper mapper, Pagination defaultPagination) {
        this(new DynamoDBRepository<>(mapper), new DynamoDBFieldBuilderDecorator(),
            new DynamoDBClassBuilderDecorator(), defaultPagination);
    }

    public DynamoDBRepositoryFactory(
        DynamoDBRepository<?, ?> repository,
        DynamoDBFieldBuilderDecorator dynamoDBFieldBuilderDecorator,
        DynamoDBClassBuilderDecorator dynamoDBClassBuilderDecorator,
        Pagination defaultPagination) {
        this.repository = repository;
        this.dynamoDBFieldBuilderDecorator = dynamoDBFieldBuilderDecorator;
        this.dynamoDBClassBuilderDecorator = dynamoDBClassBuilderDecorator;
        this.defaultPagination = defaultPagination;
    }

    @Override
    public Object getRepository(Resource<?, ?> resource) {
        return repository;
    }

    @Override
    public List<MappedFieldBuilderDecorator> getMappedFieldBuilderDecorators() {
        return Arrays.asList(dynamoDBFieldBuilderDecorator);
    }

    @Override
    public List<MappedClassBuilderDecorator> getMappedClassBuilderDecorators() {
        return Arrays.asList(dynamoDBClassBuilderDecorator);
    }

    @Override
    public Pagination getDefaultPagination() {
        return defaultPagination;
    }
}
