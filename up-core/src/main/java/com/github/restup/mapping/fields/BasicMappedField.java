package com.github.restup.mapping.fields;

import java.lang.reflect.Type;
import java.util.Objects;
import com.github.restup.annotations.field.Param;
import com.github.restup.mapping.fields.composition.CaseSensitivity;
import com.github.restup.mapping.fields.composition.Identifier;
import com.github.restup.mapping.fields.composition.Immutability;
import com.github.restup.mapping.fields.composition.Relation;
import com.github.restup.util.ReflectionUtils;

class BasicMappedField<T> implements MappedField<T> {

    private final Type type;
    private final String beanName;
    private final String apiName;
    private final String persistedName;

    private final boolean collection;
    private final boolean apiProperty;
    private final boolean transientField;

    private final String[] parameterNames;
    private final Identifier identifier;
    private final CaseSensitivity caseSensitivity;
    private final Immutability immutability;
    private final Relation relation;
    private final WritableField<Object, T> writer;
    private final ReadableField<T> reader;

    BasicMappedField(Type type, String beanName, String apiName, String persistedName,
            Identifier identifier, boolean collection, boolean apiProperty, boolean transientField, CaseSensitivity caseSensitivity,
            Relation relationship, Immutability immutability, String[] parameterNames, ReadableField<T> reader,
            WritableField<Object, T> writer) {
        this.type = type;
        this.beanName = beanName;
        this.apiName = apiName;
        this.persistedName = persistedName;
        this.collection = collection;
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
    public T newInstance() {
    		return ReflectionUtils.newInstance(type);
    }
    

    @Override
    public Type getType() {
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
    public boolean isCollection() {
    		return collection;
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
     * If this field can be set by a parameter (using {@link Param}), the accepted parameterNames
     */
    @Override
    public String[] getParameterNames() {
        return parameterNames;
    }

    @Override
    public String toString() {
        return beanName;
    }

    @Override
    public void writeValue(Object instance, T value) {
        writer.writeValue(instance, value);
    }

    @Override
    public T readValue(Object instance) {
        return reader.readValue(instance);
    }

    /**
     * @return true if this field is a member of clazz
     */
    @Override
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
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (! ( o instanceof BasicMappedField )) {
            return false;
        }
        BasicMappedField<?> that = (BasicMappedField<?>) o;
        return Objects.equals(beanName, that.beanName) && Objects.equals(reader, that.reader)
                && Objects.equals(writer, that.writer);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(beanName, reader, writer);
    }
}
