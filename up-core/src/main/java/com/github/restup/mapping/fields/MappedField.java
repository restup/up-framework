package com.github.restup.mapping.fields;

import com.github.restup.annotations.field.*;
import com.github.restup.errors.ErrorBuilder;
import com.github.restup.path.MappedFieldPathValue;
import com.github.restup.registry.Resource;
import com.github.restup.util.ReflectionUtils;
import com.github.restup.util.ReflectionUtils.BeanInfo;
import com.github.restup.util.ReflectionUtils.PropertyDescriptor;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Captures meta data about fields for mapping api
 *
 * @param <T>
 */
public class MappedField<T> implements ReadableField<T>, WritableField<T> {

    //TODO doc

    private final Class<T> type;
    private final String beanName;
    private final String apiName;
    private final String persistedName;
    private final boolean ignoreUpdateAttempt;
    private final boolean apiProperty;
    private final boolean transientField;
    private final CaseInsensitive caseInsensitive;
    private final Relationship relationship;
    private final Immutable immutable;
    private final Param param;
    private final Field field;

    protected MappedField(Class<T> type, String beanName, String apiName, String persistedName, boolean ignoreUpdateAttempt, boolean apiProperty, boolean transientField, CaseInsensitive caseInsensitive, Relationship relationship, Immutable immutable, Param param, Field field) {
        this.type = type;
        this.beanName = beanName;
        this.apiName = apiName;
        this.persistedName = persistedName;
        this.ignoreUpdateAttempt = ignoreUpdateAttempt;
        this.apiProperty = apiProperty;
        this.transientField = transientField;
        this.caseInsensitive = caseInsensitive;
        this.relationship = relationship;
        this.immutable = immutable;
        this.param = param;
        this.field = field;
    }

    /**
     * @param mappedField
     * @param resource
     * @return The relationship name from the mappedField or the resource name by default
     */
    public static String getRelationshipName(MappedField mappedField, Resource resource) {
        String name = mappedField.getRelationshipName();
        if (StringUtils.isEmpty(name)) {
            name = resource.getName();
        }
        return name;
    }

    public static <T> Builder<T> builder(Class<T> type) {
        return new Builder<T>(type);
    }

    public static Object toCaseInsensitive(MappedFieldPathValue<?> mfpv, Object value) {
        return toCaseInsensitive(mfpv.getMappedField().getCaseInsensitive(), value);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Object toCaseInsensitive(CaseInsensitive caseInsensitive, Object value) {
        if (value instanceof Collection) {
            Collection result = (Collection) ReflectionUtils.newInstance(value.getClass());
            for (Object o : (Collection) value) {
                result.add(toCaseInsensitive(caseInsensitive, o));
            }
            return result;
        } else if (value instanceof String) {
            return toCaseInsensitive((String) value, caseInsensitive.lowerCased());
        }
        return null;
    }

    private static Object toCaseInsensitive(String s, boolean lowerCased) {
        return lowerCased ? s.toLowerCase() : s.toUpperCase();
    }

    public static boolean isCaseInsensitive(MappedField<?> mf) {
        return mf != null && mf.isCaseInsensitive();
    }

    public Class<? extends Object> getDeclaringClass() {
        return field == null ? null : field.getDeclaringClass();
    }

    @SuppressWarnings("unchecked")
    public T readValue(Object o) {
        try {
            return field == null || o == null ? null : (T) field.get(o);
        } catch (IllegalAccessException e) {
            throw ErrorBuilder.buildException(e);
        } catch (IllegalArgumentException e) {
            throw ErrorBuilder.buildException(e);
        }
    }

    public void writeValue(Object obj, Object value) {
        if (obj != null) {
            try {
                field.set(obj, value);
            } catch (IllegalAccessException e) {
                ErrorBuilder.throwError(e);
            } catch (IllegalArgumentException e) {
                ErrorBuilder.throwError(e);
            }
        }
    }

    public Object getFieldInstance() {
        return ReflectionUtils.newInstance(type);
    }

    public Class<T> getType() {
        return type;
    }

    public String getBeanName() {
        return beanName;
    }

    public String getApiName() {
        return apiName;
    }

    public String getPersistedName() {
        return persistedName;
    }

    public boolean isTransientField() {
        return transientField;
    }

    public boolean isApiProperty() {
        return apiProperty;
    }

    public boolean isIgnoreUpdateAttempt() {
        return ignoreUpdateAttempt;
    }

    public Field getField() {
        return field;
    }

    public CaseInsensitive getCaseInsensitive() {
        return caseInsensitive;
    }

    public boolean isCaseInsensitive() {
        return caseInsensitive == null ? false : caseInsensitive.value();
    }

    public String getCaseInsensitiveSearchField() {
        return caseInsensitive == null || StringUtils.isBlank(caseInsensitive.searchField()) ? null : caseInsensitive.searchField();
    }

    public Class<?> getRelationshipResource() {
        return relationship == null ? null : relationship.resource();
    }

    public boolean isIncludableRelationship() {
        return relationship == null ? false : relationship.includable();
    }

    public RelationshipType getRelationshipType() {
        return relationship == null ? null : relationship.type();
    }

    public String getRelationshipName() {
        return relationship == null ? null : relationship.name();
    }

    public boolean isValidateRelationship() {
        return relationship == null ? false : relationship.validateReferences();
    }

    public String getRelationshipJoinField() {
        return relationship == null ? null : relationship.joinField();
    }

    public boolean isReadOnly() {
        return immutable == null ? false : immutable.value();
    }

    public boolean isErrorOnReadOnlyUpdateAttempt() {
        return immutable == null ? false : immutable.errorOnUpdateAttempt();
    }

    public String[] getParameterNames() {
        return param == null ? null : param.value();
    }

    @Override
    public String toString() {
        return beanName;
    }

    public T newInstance() {
        return ReflectionUtils.newInstance(type);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((field == null) ? 0 : field.hashCode());
        return result;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MappedField other = (MappedField) obj;
        if (field == null) {
            if (other.field != null)
                return false;
        } else if (!field.equals(other.field))
            return false;
        return true;
    }

    public final static class Builder<T> {

        private Class<T> type;
        private String beanName;
        private String apiName;
        private String persistedName;
        private boolean ignoreUpdateAttempt;
        private boolean apiProperty;
        private boolean transientField;
        private CaseInsensitive caseInsensitive;
        private Relationship relationship;
        private Immutable immutable;
        private Param param;
        private Field field;

        private Class<?> genericType;
        private boolean isIdField;
        private boolean nonAutoGeneratedIdValuePermitted;

        public Builder(Class<T> type) {
            this.type = type;
        }

        private Builder<T> me() {
            return this;
        }

        public Builder<T> setIdField(boolean isIdField) {
            this.isIdField = isIdField;
            return me();
        }

        public Builder<T> setNonAutoGeneratedIdValuePermitted(boolean nonAutoGeneratedIdValuePermitted) {
            this.nonAutoGeneratedIdValuePermitted = nonAutoGeneratedIdValuePermitted;
            return me();
        }

        public Builder<T> setField(Field field) {
            this.field = field;
            if (field != null && !field.isAccessible()) {
                field.setAccessible(true);
            }
            return me();
        }

        @SuppressWarnings("rawtypes")
        public Builder<T> setGenericType(Type genericType) {
            if (genericType instanceof Class) {
                return setGenericType((Class) genericType);
            }
            return me();
        }

        public Builder<T> setGenericType(Class<?> genericType) {
            this.genericType = genericType;
            return me();
        }

        public Builder<T> setApiProperty(boolean apiProperty) {
            this.apiProperty = apiProperty;
            return me();
        }

        public Builder<T> setTransientField(boolean transientField) {
            this.transientField = transientField;
            return me();
        }

        public Builder<T> setImmutable(Immutable immutable) {
            this.immutable = immutable;
            return me();
        }

        public Builder<T> setCaseInsensitive(CaseInsensitive caseInsensitive) {
            this.caseInsensitive = caseInsensitive;
            return me();
        }

        public Builder<T> setRelationship(Relationship relationship) {
            this.relationship = relationship;
            return me();
        }

        public Builder<T> setParam(Param param) {
            this.param = param;
            return me();
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        public MappedField<T> build() {
            if (isIdField) {
                return new IdentityField(type, beanName, apiName, persistedName, ignoreUpdateAttempt, apiProperty, transientField, caseInsensitive, relationship, immutable, param, field, nonAutoGeneratedIdValuePermitted);
            } else if (Iterable.class.isAssignableFrom(type)) {
                return new IterableField(type, beanName, apiName, persistedName, ignoreUpdateAttempt, apiProperty, transientField, caseInsensitive, relationship, immutable, param, field, genericType);
            }
            return new MappedField<T>(type, beanName, apiName, persistedName, ignoreUpdateAttempt, apiProperty, transientField, caseInsensitive, relationship, immutable, param, field);
        }

        public void accept(MappedFieldBuilderVisitor[] visitors, BeanInfo<T> bi, PropertyDescriptor pd) {
            // visit builders for customization
            if (visitors != null) {
                for (MappedFieldBuilderVisitor visitor : visitors) {
                    accept(visitor, bi, pd);
                }
            }
        }

        public void accept(MappedFieldBuilderVisitor visitor, BeanInfo<T> bi, PropertyDescriptor pd) {
            visitor.visit(this, bi, pd);
        }

        public String getBeanName() {
            return beanName;
        }

        public Builder<T> setBeanName(String beanName) {
            this.beanName = beanName;
            return me();
        }

        public String getApiName() {
            return apiName;
        }

        public Builder<T> setApiName(String apiName) {
            this.apiName = apiName;
            return setApiProperty(StringUtils.isNotEmpty(apiName));
        }

        public String getPersistedName() {
            return persistedName;
        }

        public Builder<T> setPersistedName(String persistedName) {
            this.persistedName = persistedName;
            return setTransientField(StringUtils.isEmpty(persistedName));
        }
    }

}
