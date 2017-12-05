package com.github.restup.mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.github.restup.mapping.fields.MappedField;
import com.github.restup.util.Assert;

/**
 * Provides an api to object to persistence mapping.
 *
 * @author andy.buttaro
 */
public interface MappedClass<T> {

    static Builder<?> builder() {
        return new Builder<>();
    }

    static <T> Builder<T> builder(Class<T> type) {
        return new Builder<T>().type(type);
    }

    /**
     * The name of the object
     */
    String getName();

    /**
     * The pluralized name of the object
     */
    String getPluralName();

    /**
     * The type of the object
     */
    Class<T> getType();

    /**
     * The type of the object's parent
     */
    Class<?> getParentType();

    /**
     * The attributes of the object
     */

    List<MappedField<?>> getAttributes();

    final static class Builder<T> {

        private String name;
        private String pluralName;
        private Class<T> type;
        private Class<?> parentType;
        private List<MappedField<?>> attributes;
        private Comparator<MappedField<?>> fieldComparator;

        Builder() {
            attributes = new ArrayList<>();
        }

        private Builder<T> me() {
            return this;
        }

        public Builder<T> name(String name) {
            this.name = name;
            return me();
        }

        public Builder<T> pluralName(String pluralName) {
            this.pluralName = pluralName;
            return me();
        }

        public Builder<T> attributes(List<MappedField<?>> attributes) {
            this.attributes = attributes;
            return me();
        }

        public Builder<T> addAttribute(MappedField<?> attribute) {
            attributes.add(attribute);
            return me();
        }

        public Builder<T> addAttribute(MappedField.Builder<?> builder) {
            return addAttribute(builder.build());
        }

        public Builder<T> addAttribute(Class<?> type, String name) {
            return addAttribute(attribute(type, name));
        }

        public Builder<T> addIdAttribute(Class<?> type, String name) {
            return addAttribute(attribute(type, name)
                    .idField(true));
        }

        private MappedField.Builder<?> attribute(Class<?> type, String name) {
            return MappedField.builder(type)
                    .apiName(name)
                    .persistedName(name)
                    .beanName(name);
        }

        public Builder<T> sortAttributesWith(Comparator<MappedField<?>> fieldComparator) {
            this.fieldComparator = fieldComparator;
            return me();
        }

        public Builder<T> parentType(Class<?> parentType) {
            this.parentType = parentType;
            return me();
        }

        Builder<T> type(Class<T> type) {
            this.type = type;
            return me();
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        public MappedClass<T> build() {
            Assert.notEmpty(name, "name is required");
            String pluralName = this.pluralName;
            if (StringUtils.isEmpty(pluralName)) {
                pluralName = name + "s";
            }
            Class<T> type = this.type;
            if (type == null) {
                type = (Class) HashMap.class;
            }
            if (fieldComparator != null) {
                Collections.sort(attributes, fieldComparator);
            }
            return new BasicMappedClass<T>(name, pluralName, type, parentType, attributes);
        }
    }
}
