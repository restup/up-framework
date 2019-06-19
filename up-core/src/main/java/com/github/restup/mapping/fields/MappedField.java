package com.github.restup.mapping.fields;

import static com.github.restup.util.ReflectionUtils.makeAccessible;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

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
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.util.Assert;
import com.github.restup.util.ReflectionUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;

/**
 * Captures meta data about fields for mapping api
 */
public interface MappedField<T> extends ReadWriteField<Object, T> {

    //TODO doc

    /**
     * 
     * @param mappedField providing relationship name
     * @param resource providing default name
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

    static Object toCaseInsensitive(CaseSensitivity caseSensitivity, Object value) {
        if (value instanceof Collection) {
            Collection result = (Collection) ReflectionUtils.newInstance(value.getClass());
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
    		return attributes.stream()
    			.filter(MappedField::isIdentifier)
    			.findFirst()
    			.get();
    }

    static <T> BasicMappedField.Builder<T> builder(Class<T> type) {
        return new BasicMappedField.Builder<>(type);
    }

    static BasicMappedField.Builder<?> builder(Type type) {
        return new BasicMappedField.Builder<>(type);
    }

    Type getType();
    
	T newInstance();

    String getBeanName();

    String getApiName();

    String getPersistedName();

    boolean isCollection();

    boolean isTransientField();

    boolean isApiProperty();

    Set<MappedIndexField> getIndexes();

    default boolean isIndexed() {
        return isNotEmpty(getIndexes());
    }

    /**
     * Indicates whether the field is sortable. A true value may still be rejected in context if the
     * underlying implementation requires additional fields to complete a query to sort
     */
    boolean isSortable();

    Identifier getIdentifier();

    default boolean isIdentifier() {
        return getIdentifier() != null;
    }

    default boolean isClientGeneratedIdentifierPermitted() {
        return applyToIdentifier(Identifier::isClientGeneratedIdentifierPermitted);
    }

    /**
     * null safe. apply f to {@link #getIdentifier()}
     * 
     * @param <R> result of function
     * @param f function to apply
     * @return result of function
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
     * 
     * @param <R> result of function
     * @param f function to apply
     * @return result of function
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
     * 
     * @param <R> result of function
     * @param f function to apply
     * @return result of function
     */
    default <R> R applyToImmutability(Function<Immutability, R> f) {
        Immutability immutability = getImmutability();
        return immutability == null ? null : f.apply(immutability);
    }

    String[] getParameterNames();

    Relation getRelationship();

    boolean isRelationship();

    @Override
    boolean isDeclaredBy(Class<?> clazz);

    default String getRelationshipName() {
        return applyToRelationship(Relation::getName);
    }

    default String getRelationshipResource(ResourceRegistry registry) {
        return applyToRelationship(r -> r.getResource(registry));
    }

    default String getRelationshipJoinField() {
        return applyToRelationship(Relation::getJoinField);
    }

    default RelationshipType getRelationshipType() {
        return applyToRelationship(Relation::getType);
    }

    /**
     * null safe. apply f to {@link #getRelationship()}
     * 
     * @param <R> result of function
     * @param f function to apply to the relationship
     * @return result of f
     */
    default <R> R applyToRelationship(Function<Relation, R> f) {
        Relation relation = getRelationship();
        return relation == null ? null : f.apply(relation);
    }

    final class Builder<T> {

        private Type type;
        private String beanName;
        private String apiName;
        private String persistedName;
        private boolean apiProperty;
        private boolean transientField;
        private boolean sortable = true;
        private Identifier identifier;
        private CaseSensitivity caseSensitivity;
        private Relation relation;
        private Immutability immutability;
        private String[] parameterNames;
        private Field field;
        private Method getter;
        private Method setter;
        private Map<String, Short> indexes = new HashMap<>();

        private Class<?> genericType;

        public Builder(Type type) {
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

        public Builder<T> sortable(boolean sortable) {
            this.sortable = sortable;
            return me();
        }

        public Builder<T> immutable(Immutable immutable) {
            return immutability(Immutability.getImmutability(immutable));
        }

        public Builder<T> immutability(Immutability immutability) {
            this.immutability = immutability;
            return me();
        }

        public Builder<T> index(String name, int position) {
            if (StringUtils.isNotEmpty(name)) {
                indexes.put(name, Integer.valueOf(position).shortValue());
            }
            return me();
        }

        public Builder<?> caseSensitiviteField(String searchField) {
            return caseSensitivity(CaseSensitivity.builder()
                .searchField(searchField));
        }

        public Builder<T> caseInsensitive(CaseInsensitive caseInsensitive) {
            return caseSensitivity(CaseSensitivity.getCaseSensitivity(caseInsensitive));
        }

        public Builder<T> caseSensitivity(CaseSensitivity.Builder caseSensitivity) {
            return caseSensitivity(caseSensitivity.build());
        }

        public Builder<T> caseSensitivity(CaseSensitivity caseSensitivity) {
            this.caseSensitivity = caseSensitivity;
            return me();
        }

        public Builder<T> relationship(Relationship relationship) {
            return relation(Relation.getRelation(relationship));
        }

        public Builder<T> relationshipTo(String resource) {
        		return relationshipTo(resource, "id");
        }

        public Builder<T> relationshipTo(String resource, String joinField) {
        		return relation(Relation.builder()
        				.joinField(joinField)
					.resource(resource).build());
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

        public MappedField<T> build() {

            if (apiProperty) {
                Assert.notNull(apiProperty, "api name is required for api fields");
	    		}
            if (!transientField) {
                Assert
                    .notNull(persistedName, "persisted name is required for non transient fields");
	    		}

            ReadableField readable = null;
            WritableField<Object, ?> writable = null;
            if (readable == null) {
                if (field != null) {
                    readable = ReflectMappedField.of(field);
                } else if (getter != null && setter != null) {
                    readable = ReflectMappedMethod.of(getter, setter);
                } else if (getter != null) {
                    readable = ReflectReadableMappedMethod.of(getter);
                } else {
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
            Set<MappedIndexField> indexes = new HashSet<>();
            for (Entry<String, Short> e : this.indexes.entrySet()) {
                if (StringUtils.isNotBlank(e.getKey())) {
                    indexes.add(MappedIndexField.of(e.getKey(), e.getValue()));
                }
            }

            if ( type instanceof Class ) {
            		Class clazz = (Class) type;
	            if (Iterable.class.isAssignableFrom(clazz)) {
	            		boolean collection = Collection.class.isAssignableFrom(clazz);
                  return new BasicIterableField(clazz, beanName, apiName, persistedName, identifier,
                      indexes,
                      collection, apiProperty, transientField, sortable, caseSensitivity, relation,
                      immutability, parameterNames, readable, writable, genericType);
              }
            }

            indexes = Collections.unmodifiableSet(indexes);
            return new BasicMappedField(type, beanName, apiName, persistedName, identifier,
                indexes,
                false,
                apiProperty, transientField, sortable, caseSensitivity, relation, immutability,
                parameterNames, readable, writable);
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

		public void anonymousMapping() {
        beanName = nvl(beanName, apiName, persistedName);
        if (!transientField) {
				persistedName = nvl(persistedName, beanName);
			}
			if ( apiProperty ) {
				apiName = nvl(apiName, beanName);
			}
		}

		private String nvl(String... vals) {
			for (String s : vals) {
				if ( StringUtils.isNotEmpty(s) ) {
					return s;
				}
			}
			return null;
		}
    }

}
