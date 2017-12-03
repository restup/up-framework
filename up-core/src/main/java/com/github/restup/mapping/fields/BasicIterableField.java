package com.github.restup.mapping.fields;

import java.io.Serializable;

import com.github.restup.mapping.fields.composition.CaseSensitivity;
import com.github.restup.mapping.fields.composition.Identifier;
import com.github.restup.mapping.fields.composition.Immutability;
import com.github.restup.mapping.fields.composition.Relation;

/**
 * {@link MappedField} representing an {@link Iterable} type, capturing detail of the generic type of items
 *
 * @param <ID>
 */
class BasicIterableField<ID extends Serializable> extends BasicMappedField<ID> implements IterableField<ID> {


	private final Class<?> genericType;

    BasicIterableField(Class<ID> type, String beanName, String apiName, String persistedName, Identifier identifier,
			boolean apiProperty, boolean transientField, CaseSensitivity caseSensitivity, Relation relationship,
			Immutability immutability, String[] parameterNames, ReadableField reader,
			WritableField<Object, ID> writer, Class<?> genericType) {
		super(type, beanName, apiName, persistedName, identifier, apiProperty, transientField, caseSensitivity, relationship,
				immutability, parameterNames, reader, writer);
		this.genericType = genericType;
	}

    public Class<?> getGenericType() {
        return genericType;
    }

}
