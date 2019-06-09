package com.github.restup.mapping.fields;

import com.github.restup.mapping.fields.decorators.IdentityByConventionMappedFieldBuilderDecorator;
import com.github.restup.mapping.fields.decorators.JacksonMappedFieldBuilderDecorator;
import com.github.restup.registry.settings.AutoDetectConstants;
import com.github.restup.util.ReflectionUtils.BeanInfo;
import com.github.restup.util.ReflectionUtils.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Allows visiting a builder while building a {@link MappedField} using {@link
 * DefaultMappedFieldFactory} for handling api, persistence, or other custom annotations.
 *
 * @author abuttaro
 */
@FunctionalInterface
public interface MappedFieldBuilderDecorator {

    static Builder builder() {
        return new Builder();
    }

    /**
     * Decorate a {@link MappedField.Builder} during field mapping to provide customization of field
     * attributes
     *
     * @param b builder to decorate
     * @param bi describing the Class being mapped
     * @param pd describing the property being mapped
     * @param <T> type of class being mapped
     */
    <T> void decorate(MappedField.Builder<T> b, BeanInfo<T> bi, PropertyDescriptor pd);

    class Builder {

        private boolean defaults;
        private List<MappedFieldBuilderDecorator> decorators = new ArrayList<>();

        private Builder() {

        }

        private Builder me() {
            return this;
        }

        public Builder withDefaults() {
            return withDefaults(true);
        }

        public Builder withDefaults(boolean defaults) {
            this.defaults = defaults;
            return me();
        }

        public Builder withIdentityConvention(String identityField) {
            return add(new IdentityByConventionMappedFieldBuilderDecorator(identityField));
        }

        public Builder add(MappedFieldBuilderDecorator decorator) {
            decorators.add(decorator);
            return me();
        }

        public Builder addAll(Collection<MappedFieldBuilderDecorator> decorators) {
            if (decorators != null) {
                decorators.stream().forEach(this::add);
            }
            return me();
        }

        public Builder addSuppliers(Object... arr) {
            for (Object o : arr) {
                if (o instanceof MappedFieldBuilderDecoratorSupplier) {
                    addAll(((MappedFieldBuilderDecoratorSupplier) o)
                        .getMappedFieldBuilderDecorators());
                }
            }
            return me();
        }

        public MappedFieldBuilderDecorator[] build() {
            List<MappedFieldBuilderDecorator> result = new ArrayList<>(decorators);
            if (defaults) {
                result.add(new IdentityByConventionMappedFieldBuilderDecorator());

                if (AutoDetectConstants.JACKSON2_EXISTS) {
                    result.add(new JacksonMappedFieldBuilderDecorator());
                }
            }
            return result.toArray(new MappedFieldBuilderDecorator[0]);
        }
    }


}
