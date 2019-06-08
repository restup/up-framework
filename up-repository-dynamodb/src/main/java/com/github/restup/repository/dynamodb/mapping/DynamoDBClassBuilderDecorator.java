package com.github.restup.repository.dynamodb.mapping;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.github.restup.mapping.MappedClass;
import com.github.restup.mapping.MappedClassBuilderDecorator;
import com.github.restup.util.ReflectionUtils.BeanInfo;

public class DynamoDBClassBuilderDecorator implements MappedClassBuilderDecorator {

    @Override
    public <T> void decorate(MappedClass.Builder<T> builder, BeanInfo<T> bi) {

        DynamoDBTable dynamoDBTable = bi.getType().getAnnotation(DynamoDBTable.class);
        if (dynamoDBTable != null) {
            builder.persistedName(dynamoDBTable.tableName());
        }
    }
}
