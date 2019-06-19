package com.github.restup.mapping;

import com.github.restup.annotations.model.CreateStrategy;
import com.github.restup.annotations.model.DeleteStrategy;
import com.github.restup.annotations.model.UpdateStrategy;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.util.Assert;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * Provides an api to object to persistence mapping.
 *
 * @author andy.buttaro
 */
public interface MappedClass<T> {

    static Builder<Object> builder() {
        return new AnonymousBuilder();
    }

    static <T> Builder<T> builder(Class<T> type) {
        return new Builder<>(type);
    }

    static Comparator<MappedField<?>> getDefaultFieldComparator() {
        return new DefaultMappedFieldComparator();
    }

    /**
     * @return The name of the object
     */
    String getName();

    /**
     * @return The pluralized name of the object
     */
    String getPluralName();

    /**
     * @return The persisted name of the object
     */
    String getPersistedName();

    /**
     * @return The type of the object
     */
    Type getType();

    /**
     * @return The type of the object's parent
     */
    Type getParentType();

    /**
     * If nested properties of this mapping have a {@link java.util.Map} with typed properties then
     * this should return true.
     *
     * @return true if the model contains a {@link java.util.Map} with typed properties
     */
    boolean isTypedMapPresent();

    /**
     * @return The attributes of the object
     */
    List<MappedField<?>> getAttributes();

    /**
     * @return a new instance of {@link #getType()}
     */
    T newInstance();

    /**
     * @return true if only indexed queries should be permitted
     */
    boolean isIndexedQueryOnly();

    CreateStrategy getCreateStrategy();

    UpdateStrategy getUpdateStrategy();

    DeleteStrategy getDeleteStrategy();

    class AnonymousBuilder extends Builder<Object> {

        @Override
        public Builder<Object> addAttribute(MappedField.Builder<?> builder) {
            builder.anonymousMapping();
            return super.addAttribute(builder);
        }
    }

    class Builder<T> {

        private final Type type;
        private String name;
        private String pluralName;
        private String persistedName;
        private Type parentType;
        private List<MappedField<?>> attributes;
        private Comparator<MappedField<?>> fieldComparator;
        private boolean indexedQueryOnly;
        private CreateStrategy createStrategy = CreateStrategy.DEFAULT;
        private UpdateStrategy updateStrategy = UpdateStrategy.DEFAULT;
        private DeleteStrategy deleteStrategy = DeleteStrategy.DEFAULT;

        Builder(Type type) {
            Assert.notNull(type, "type is required");
            this.type = type;
            attributes = new ArrayList<>();
            indexedQueryOnly = true;
        }

        Builder() {
            this(new UntypedClass<>());
        }

        private Builder<T> me() {
            return this;
        }

        public Builder<T> name(String name) {
            this.name = name;
            return me();
        }

        public Builder<T> defaultName(String name) {
            if (StringUtils.isEmpty(this.name)) {
                return name(name);
            }
            return me();
        }

        public Builder<T> pluralName(String pluralName) {
            this.pluralName = pluralName;
            return me();
        }

        public Builder<T> persistedName(String persistedName) {
            this.persistedName = persistedName;
            return me();
        }

        public Builder<T> indexedQueryOnly(Boolean indexedQueryOnly) {
            this.indexedQueryOnly = indexedQueryOnly;
            return me();
        }

        public Builder<T> createStrategy(CreateStrategy createStrategy) {
            this.createStrategy = createStrategy;
            return me();
        }

        public Builder<T> updateStrategy(UpdateStrategy updateStrategy) {
            this.updateStrategy = updateStrategy;
            return me();
        }

        public Builder<T> deleteStrategy(DeleteStrategy deleteStrategy) {
            this.deleteStrategy = deleteStrategy;
            return me();
        }

        public Builder<T> attributes(List<MappedField<?>> attributes) {
            this.attributes = attributes;
            return me();
        }

        Builder<T> addAttribute(MappedField<?> attribute) {
            attributes.add(attribute);
            return me();
        }

        public Builder<T> addAttribute(MappedField.Builder<?> builder) {
            return addAttribute(builder.build());
        }

        public Builder<T> addAttribute(Class<?> type, String name) {
            return addAttribute(attribute(type, name));
        }

        public Builder<T> addCaseInsensitiveAttribute(String name, String lowerCaseNameField) {
            return addAttribute(MappedField.builder(String.class)
                .apiName(name)
                .caseSensitiviteField(lowerCaseNameField))
                // add lowerCaseNameField
                .addAttribute(MappedField.builder(String.class)
                    .beanName(lowerCaseNameField));
        }

        public Builder<T> id(Class<?> type) {
            return id(type, "id");
        }

        public Builder<T> id(Class<?> type, String name) {
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

        public MappedClass<T> build() {
            Assert.notEmpty(name, "name is required");
            String pluralName = this.pluralName;
            String persistedName = this.persistedName;
            boolean containsTypedMap = type instanceof UntypedClass;
            if (StringUtils.isEmpty(pluralName)) {
                pluralName = name + "s";
            }
            if (StringUtils.isEmpty(persistedName)) {
                persistedName = name;
            }
            if (fieldComparator != null) {
                Collections.sort(attributes, fieldComparator);
            }
            return new BasicMappedClass<>(name, pluralName, persistedName, type, parentType,
                attributes, containsTypedMap, indexedQueryOnly, createStrategy, updateStrategy,
                deleteStrategy);
        }

    }

}
