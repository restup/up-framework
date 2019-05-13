package com.github.restup.mapping.fields;

import com.github.restup.mapping.fields.composition.CaseSensitivity;
import com.github.restup.mapping.fields.composition.Identifier;
import com.github.restup.mapping.fields.composition.Immutability;
import com.github.restup.mapping.fields.composition.Relation;
import java.util.Set;

/**
 * {@link MappedField} representing an {@link Iterable} type, capturing detail
 * of the generic type of items
 */
class BasicIterableField<T> extends BasicMappedField<T> implements IterableField<T> {

	private final Class<?> genericType;

	BasicIterableField(Class<T> type, String beanName, String apiName, String persistedName,
		Identifier identifier, Set<MappedIndexField> indexes,
		boolean collection, boolean apiProperty, boolean transientField, boolean sortable,
		CaseSensitivity caseSensitivity,
			Relation relationship, Immutability immutability, String[] parameterNames, ReadableField<T> reader,
			WritableField<Object, T> writer, Class<?> genericType) {
		super(type, beanName, apiName, persistedName, identifier, indexes, collection, apiProperty,
			transientField, sortable,
				caseSensitivity, relationship, immutability, parameterNames, reader, writer);
		this.genericType = genericType;
	}

	@Override
    public Class<?> getGenericType() {
		return genericType;
	}

}
