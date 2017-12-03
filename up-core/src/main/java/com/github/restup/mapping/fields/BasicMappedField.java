package com.github.restup.mapping.fields;

import java.util.Objects;

import com.github.restup.annotations.field.Param;
import com.github.restup.mapping.fields.composition.CaseSensitivity;
import com.github.restup.mapping.fields.composition.Identifier;
import com.github.restup.mapping.fields.composition.Immutability;
import com.github.restup.mapping.fields.composition.Relation;

class BasicMappedField<T> implements MappedField<T> {

	private final Class<T> type;
	private final String beanName;
	private final String apiName;
	private final String persistedName;

	private final boolean apiProperty;
	private final boolean transientField;

	private final String[] parameterNames;

	private WritableField<Object, T> writer;
	private ReadableField reader;
	private final Identifier identifier;
	private final CaseSensitivity caseSensitivity;
	private final Immutability immutability;
	private final Relation relation;

	BasicMappedField(Class<T> type, String beanName, String apiName, String persistedName,
			Identifier identifier, boolean apiProperty, boolean transientField, CaseSensitivity caseSensitivity,
			Relation relationship, Immutability immutability, String[] parameterNames, ReadableField reader,
			WritableField<Object, T> writer) {
		this.type = type;
		this.beanName = beanName;
		this.apiName = apiName;
		this.persistedName = persistedName;
		this.apiProperty = apiProperty;
		this.transientField = transientField;
		this.parameterNames = parameterNames;

		this.identifier = identifier;
		this.reader = reader;
		this.writer = writer;
		this.caseSensitivity = caseSensitivity;
		this.immutability = immutability;
		this.relation = relationship;
	}

	@Override
	public Class<T> getType() {
		return type;
	}

	@Override
	public String getBeanName() {
		return beanName;
	}

	@Override
	public String getApiName() {
		return apiName;
	}

	@Override
	public String getPersistedName() {
		return persistedName;
	}

	@Override
	public boolean isTransientField() {
		return transientField;
	}

	@Override
	public boolean isApiProperty() {
		return apiProperty;
	}

	@Override
	public Identifier getIdentifier() {
		return identifier;
	}

	@Override
	public CaseSensitivity getCaseSensitivity() {
		return caseSensitivity;
	}

	@Override
	public Immutability getImmutability() {
		return immutability;
	}

	@Override
	public Relation getRelationship() {
		return relation;
	}

	@Override
	public boolean isRelationship() {
		return relation != null;
	}

	/**
	 * If this field can be set by a parameter (using {@link Param}), the accepted
	 * parameterNames
	 * 
	 * @return
	 */
	@Override
	public String[] getParameterNames() {
		return parameterNames;
	}

	@Override
	public String toString() {
		return beanName;
	}

	public void writeValue(Object instance, T value) {
		writer.writeValue(instance, value);
	}

	public Object readValue(Object instance) {
		return reader.readValue(instance);
	}

	/**
	 * @param clazz
	 * @return true if this field is a member of clazz
	 */
	public boolean isDeclaredBy(Class<?> clazz) {
		if (reader instanceof DeclaredBy) {
			return ((DeclaredBy) reader).isDeclaredBy(clazz);
		}
		if (writer instanceof DeclaredBy) {
			return ((DeclaredBy) writer).isDeclaredBy(clazz);
		}
		return false;
	}

	@Override
	public Object createDeclaringInstance() {
		return writer.createDeclaringInstance();
	}
	
	@Override
	public T createInstance() {
		return writer.createInstance();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		BasicMappedField<?> that = (BasicMappedField<?>) o;
		return Objects.equals(beanName, that.beanName) && Objects.equals(reader, that.reader)
				&& Objects.equals(writer, that.writer);
	}

	@Override
	public int hashCode() {
		return Objects.hash(beanName, reader, writer);
	}
}
