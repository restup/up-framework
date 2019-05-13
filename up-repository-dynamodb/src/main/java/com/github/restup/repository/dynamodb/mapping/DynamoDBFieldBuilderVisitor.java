package com.github.restup.repository.dynamodb.mapping;

import static com.github.restup.util.ReflectionUtils.getAnnotation;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.mapping.fields.MappedFieldBuilderVisitor;
import com.github.restup.util.ReflectionUtils.BeanInfo;
import com.github.restup.util.ReflectionUtils.PropertyDescriptor;

/**
 * {@link MappedFieldBuilderVisitor} whic
 */
public class DynamoDBFieldBuilderVisitor implements MappedFieldBuilderVisitor {

    public final static String PRIMARY_KEY = "~primary~";

    /**
     * Checks for {@link JsonProperty} and applies the api property name to builder if the
     * annotation exists
     */
    @Override
    public <T> void visit(MappedField.Builder<T> b, BeanInfo<T> bi, PropertyDescriptor pd) {
        DynamoDBIgnore ignore = getAnnotation(DynamoDBIgnore.class, pd);
        if (ignore != null) {
            b.persistedName(null).sortable(false);
            return;
        }
        DynamoDBAttribute attribute = getAnnotation(DynamoDBAttribute.class, pd);
        if (attribute != null) {
            b.persistedName(attribute.attributeName())
                .sortable(false);
            return;
        }
        int position = 0;
        DynamoDBHashKey hashKey = getAnnotation(DynamoDBHashKey.class, pd);
        if (hashKey != null) {
            b.persistedName(hashKey.attributeName())
                .sortable(false)
                .index(PRIMARY_KEY, position);
        }

        DynamoDBIndexHashKey indexHashKey = getAnnotation(DynamoDBIndexHashKey.class, pd);
        if (indexHashKey != null) {
            b.persistedName(indexHashKey.attributeName())
                .sortable(false)
                .index(indexHashKey.globalSecondaryIndexName(), position);
            for (String idx : indexHashKey.globalSecondaryIndexNames()) {
                b.index(idx, position);
            }
        }

        position = 1;
        DynamoDBRangeKey rangeKey = getAnnotation(DynamoDBRangeKey.class, pd);
        if (rangeKey != null) {
            b.persistedName(rangeKey.attributeName())
                .sortable(true)
                .index(PRIMARY_KEY, position);
        }

        DynamoDBIndexRangeKey indexRangeKey = getAnnotation(DynamoDBIndexRangeKey.class, pd);
        if (indexRangeKey != null) {
            b.persistedName(indexRangeKey.attributeName())
                .sortable(true)
                .index(indexRangeKey.globalSecondaryIndexName(), position);

            for (String idx : indexRangeKey.globalSecondaryIndexNames()) {
                b.index(idx, position);
            }
        }

    }


}
