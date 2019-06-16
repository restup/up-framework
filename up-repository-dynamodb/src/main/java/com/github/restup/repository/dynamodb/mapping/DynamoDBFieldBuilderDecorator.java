package com.github.restup.repository.dynamodb.mapping;

import static com.github.restup.util.ReflectionUtils.getAnnotation;
import static com.github.restup.util.UpUtils.ifEmpty;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.mapping.fields.MappedFieldBuilderDecorator;
import com.github.restup.util.ReflectionUtils.BeanInfo;
import com.github.restup.util.ReflectionUtils.PropertyDescriptor;

/**
 * {@link MappedFieldBuilderDecorator} whic
 */
public class DynamoDBFieldBuilderDecorator implements MappedFieldBuilderDecorator {

    public final static String PRIMARY_KEY = "~primary~";

    /**
     * Checks for {@link JsonProperty} and applies the api property name to builder if the
     * annotation exists
     */
    @Override
    public <T> void decorate(MappedField.Builder<T> b, BeanInfo<T> bi, PropertyDescriptor pd) {
        DynamoDBIgnore ignore = getAnnotation(DynamoDBIgnore.class, pd);
        String persistedName = pd.getName();
        if (ignore != null) {
            b.persistedName(null).sortable(false);
            return;
        }
        DynamoDBAttribute attribute = getAnnotation(DynamoDBAttribute.class, pd);
        if (attribute != null) {
            persistedName = attribute.attributeName();
            b.sortable(false);
        }
        int position = 0;
        DynamoDBHashKey hashKey = getAnnotation(DynamoDBHashKey.class, pd);
        if (hashKey != null) {
            persistedName = ifEmpty(hashKey.attributeName(), persistedName);
            b.sortable(false)
                .index(PRIMARY_KEY, position);
        }

        DynamoDBIndexHashKey indexHashKey = getAnnotation(DynamoDBIndexHashKey.class, pd);
        if (indexHashKey != null) {
            persistedName = ifEmpty(indexHashKey.attributeName(), persistedName);
            b.sortable(false)
                .index(indexHashKey.globalSecondaryIndexName(), position);
            for (String idx : indexHashKey.globalSecondaryIndexNames()) {
                b.index(idx, position);
            }
        }

        position = 1;
        DynamoDBRangeKey rangeKey = getAnnotation(DynamoDBRangeKey.class, pd);
        if (rangeKey != null) {
            persistedName = ifEmpty(rangeKey.attributeName(), persistedName);
            b.sortable(true)
                .index(PRIMARY_KEY, position);
        }

        DynamoDBIndexRangeKey indexRangeKey = getAnnotation(DynamoDBIndexRangeKey.class, pd);
        if (indexRangeKey != null) {
            persistedName = ifEmpty(indexRangeKey.attributeName(), persistedName);
            b.sortable(true)
                .index(indexRangeKey.globalSecondaryIndexName(), position);

            for (String idx : indexRangeKey.globalSecondaryIndexNames()) {
                b.index(idx, position);
            }
        }
        b.persistedName(persistedName);
    }


}
