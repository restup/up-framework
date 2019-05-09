package com.github.restup.repository.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.github.restup.registry.Resource;
import com.github.restup.repository.RepositoryFactory;

public class DynamoDBRepositoryFactory implements RepositoryFactory {

    protected DynamoDBRepository<?, ?> repository;

    public DynamoDBRepositoryFactory(DynamoDBMapper mapper) {
        this(new DynamoDBRepository<>(mapper));
    }

    public DynamoDBRepositoryFactory(DynamoDBRepository<?, ?> repository) {
        this.repository = repository;
    }

    @Override
    public Object getRepository(Resource<?, ?> resource) {
        return repository;
    }

}
