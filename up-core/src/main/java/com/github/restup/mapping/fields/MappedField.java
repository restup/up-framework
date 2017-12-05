package com.github.restup.mapping.fields;

import static com.github.restup.util.ReflectionUtils.makeAccessible;
import static com.github.restup.util.ReflectionUtils.newInstance;

import com.github.restup.annotations.field.CaseInsensitive;
import com.github.restup.annotations.field.Immutable;
import com.github.restup.annotations.field.Param;
import com.github.restup.annotations.field.Relationship;
import com.github.restup.annotations.field.RelationshipType;
import com.github.restup.mapping.fields.composition.CaseSensitivity;
import com.github.restup.mapping.fields.composition.Identifier;
import com.github.restup.mapping.fields.composition.Immutability;
import com.github.restup.mapping.fields.composition.MapField;
import com.github.restup.mapping.fields.composition.ReflectMappedField;
import com.github.restup.mapping.fields.composition.ReflectMappedMethod;
import com.github.restup.mapping.fields.composition.ReflectReadableMappedMethod;
import com.github.restup.mapping.fields.composition.ReflectWritableMappedMethod;
import com.github.restup.mapping.fields.composition.Relation;
import com.github.restup.path.MappedFieldPathValue;
import com.github.restup.registry.Resource;
import com.github.restup.util.ReflectionUtils.BeanInfo;
import com.github.restup.util.ReflectionUtils.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;

/**
 * Captures meta data about fields for mapping api
 */
public interface MappedField<T> extends ReadWriteField<Object, T> {

    //TODO doc

    /**
     * @return The relationship name from the mappedField or the resource name by default
     */
    static String getRelationshipName(MappedField<?> mappedField, Resource<?, ?> resource) {
        String name = mappedField.getRelationshipName();
        if (StringUtils.isEmpty(name)) {
            name = resource.getName();
        }
        return name;
    }

    static Object toCaseInsensitive(MappedFieldPathValue<?> mfpv, Object value) {
        return toCaseInsensitive(mfpv.getMappedField().getCaseSensitivity(), value);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    static Object toCaseInsensitive(CaseSensitivity caseSensitivity, Object value) {
        if (value instanceof Collection) {
            Collection result = (Collection) newInstance(value.getClass());
            for (Object o : (Collection) value) {
                result.add(toCaseInsensitive(caseSensitivity, o));
            }
            return result;
        } else if (value instanceof String) {
            String s = (String) value;
            return caseSensitivity.isLowerCased() ? s.toLowerCase() : s.toUpperCase();
        }
        return null;
    }

    static boolean isCaseInsensitive(MappedField<?> mf) {
        return mf != null && mf.isCaseInsensitive();
    }

    static MappedField<?> getIdentityField(List<MappedField<?>> attributes) {
        for (MappedField<?> field : attributes) {
            if (field.isIdentifier()) {
                return field;
            }
        }
        return null;
    }

    static <T> BasicMappedField.Builder<T> builder(Class<T> type) {
        return new BasicMappedField.Builder<T>(type);
    }

    Class<T> getType();

    String getBeanName();

    String getApiName();

    String getPersistedName();

    boolean isTransientField();

    boolean isApiProperty();

    Identifier getIdentifier();

    default boolean isIdentifier() {
        return getIdentifier() != null;
    }

    default boolean isIdentifierNonAutoGeneratedValuePermitted() {
        return applyToIdentifier(Identifier::isNonAutoGeneratedValuePermitted);
    }

    /**
     * null safe. apply f to {@link #getIdentifier()}
     */
    default <R> R applyToIdentifier(Function<Identifier, R> f) {
        Identifier identifier = getIdentifier();
        return identifier == null ? null : f.apply(identifier);
    }

    CaseSensitivity getCaseSensitivity();

    default boolean isCaseInsensitive() {
        return applyToCaseSensitivity(CaseSensitivity::isCaseInsensitive) == Boolean.TRUE;
    }

    default String getCaseInsensitiveSearchField() {
        return applyToCaseSensitivity(CaseSensitivity::getSearchField);
    }

    /**
     * null safe. apply f to {@link #getCaseSensitivity()}
     */
    default <R> R applyToCaseSensitivity(Function<CaseSensitivity, R> f) {
        CaseSensitivity caseSensitivity = getCaseSensitivity();
        return caseSensitivity == null ? null : f.apply(caseSensitivity);
    }

    Immutability getImmutability();

    default boolean isImmutable() {
        return applyToImmutability(Immutability::isImmutable) == Boolean.TRUE;
    }

    default boolean isImmutabilityErrorOnUpdateAttempt() {
        return applyToImmutability(Immutability::isErrorOnUpdateAttempt) == Boolean.TRUE;
    }

    default boolean isImmutabilityIgnoreUpdateAttempt() {
        return applyToImmutability(Immutability::isIgnoreUpdateAttempt) == Boolean.TRUE;
    }

    /**
     * null safe. apply f to {@link #getImmutability()}
     */
    default <R> R applyToImmutability(Function<Immutability, R> f) {
        Immutability immutability = getImmutability();
        return immutability == null ? null : f.apply(immutability);
    }

    String[] getParameterNames();

    Relation getRelationship();

    boolean isRelationship();

    boolean isDeclaredBy(Class<?> clazz);

    default String getRelationshipName() {
        return applyToRelationship(Relation::getName);
    }

    default Class<?> getRelationshipResource() {
        return applyToRelationship(Relation::getResource);
    }

    default String getRelationshipJoinField() {
        return applyToRelationship(Relation::getJoinField);
    }

    default RelationshipType getRelationshipType() {
        return applyToRelationship(Relation::getType);
    }

    /**
     * null safe. apply f to {@link #getRelationship()}
     */
    default <R> R applyToRelationship(Function<Relation, R> f) {
        Relation relation = getRelationship();
        return relation == null ? null : f.apply(relation);
    }

    public final static class Builder<T> {

        private Class<T> type;
        private String beanName;
        private String apiName;
        private String persistedName;
        private boolean apiProperty;
        private boolean transientField;
        private Identifier identifier;
        private CaseSensitivity caseSensitivity;
        private Relation relation;
        private Immutability immutability;
        private String[] parameterNames;
        private Field field;
        private Method getter;
        private Method setter;

        private Class<?> genericType;

        public Builder(Class<T> type) {
            this.type = type;
        }

        private Builder<T> me() {
            return this;
        }

        public Builder<T> field(Field field) {
            this.field = makeAccessible(field);
            return me();
        }

        public Builder<T> getter(Method getter) {
            this.getter = makeAccessible(getter);
            return me();
        }

        public Builder<T> setter(Method setter) {
            this.setter = makeAccessible(setter);
            return me();
        }

        @SuppressWarnings("rawtypes")
        public Builder<T> genericType(Type genericType) {
            if (genericType instanceof Class) {
                return genericType((Class) genericType);
            }
            return me();
        }

        public Builder<T> genericType(Class<?> genericType) {
            this.genericType = genericType;
            return me();
        }

        public Builder<T> apiProperty(boolean apiProperty) {
            this.apiProperty = apiProperty;
            return me();
        }

        public Builder<T> transientField(boolean transientField) {
            this.transientField = transientField;
            return me();
        }

        public Builder<T> immutable(Immutable immutable) {
            return immutability(Immutability.getImmutability(immutable));
        }

        public Builder<T> immutability(Immutability immutability) {
            this.immutability = immutability;
            return me();
        }

        public Builder<T> caseInsensitive(CaseInsensitive caseInsensitive) {
            return caseSensitivity(CaseSensitivity.getCaseSensitivity(caseInsensitive));
        }

        public Builder<T> caseSensitivity(CaseSensitivity caseSensitivity) {
            this.caseSensitivity = caseSensitivity;
            return me();
        }

        public Builder<T> relationship(Relationship relationship) {
            return relation(Relation.getRelation(relationship));
        }

        public Builder<T> relation(Relation relation) {
            this.relation = relation;
            return me();
        }

        public Builder<T> param(Param param) {
            return parameterNames(param == null ? null : param.value());
        }

        public Builder<T> parameterNames(String... parameterNames) {
            this.parameterNames = parameterNames;
            return me();
        }

        public Builder<T> identifier(Identifier identifier) {
            this.identifier = identifier;
            return me();
        }

        public Builder<T> idField(boolean isIdField) {
            return identifier(isIdField ? Identifier.builder().build() : null);
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        public MappedField<T> build() {

            ReadableField readable = null;
            WritableField<Object, ?> writable = null;
            if (readable == null) {
                if (field != null) {
                    readable = ReflectMappedField.of(field);
                } else if (getter != null && setter != null) {
                    readable = ReflectMappedMethod.of(getter, setter);
                } else if (getter != null) {
                    readable = ReflectReadableMappedMethod.of(getter);
                } else if (writable == null) {
                    // if writable is explicitly configured (not null) then
                    // we will use setter or used default MapField readable
                    if (setter != null) {
                        writable = (WritableField) ReflectWritableMappedMethod.of(setter);
                    } else {
                        readable = MapField.of(beanName);
                    }
                }
            }

            if (writable == null && readable instanceof WritableField) {
                writable = (WritableField) readable;
            }

            Immutability immutability = this.immutability;
            if (identifier != null && immutability == null) {
                immutability = Immutability.builder().build();
            }

            if (Iterable.class.isAssignableFrom(type)) {
                return new BasicIterableField(type, beanName, apiName, persistedName, identifier, apiProperty, transientField, caseSensitivity, relation, immutability, parameterNames, readable, writable, genericType);
            }
            return new BasicMappedField(type, beanName, apiName, persistedName, identifier, apiProperty, transientField, caseSensitivity, relation, immutability, parameterNames, readable, writable);
        }

        public void accept(MappedFieldBuilderVisitor[] visitors, BeanInfo<T> bi,
                PropertyDescriptor pd) {
            // visit builders for customization
            if (visitors != null) {
                for (MappedFieldBuilderVisitor visitor : visitors) {
                    accept(visitor, bi, pd);
                }
            }
        }

        public void accept(MappedFieldBuilderVisitor visitor, BeanInfo<T> bi,
                PropertyDescriptor pd) {
            visitor.visit(this, bi, pd);
        }

        public String getBeanName() {
            return beanName;
        }

        public Builder<T> beanName(String beanName) {
            this.beanName = beanName;
            return me();
        }

        public String getApiName() {
            return apiName;
        }

        public Builder<T> apiName(String apiName) {
            this.apiName = apiName;
            return apiProperty(StringUtils.isNotEmpty(apiName));
        }

        public String getPersistedName() {
            return persistedName;
        }

        public Builder<T> persistedName(String persistedName) {
            this.persistedName = persistedName;
            return transientField(StringUtils.isEmpty(persistedName));
        }
    }
}
