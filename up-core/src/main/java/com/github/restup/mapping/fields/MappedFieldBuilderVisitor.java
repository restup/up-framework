package com.github.restup.mapping.fields;

import com.github.restup.mapping.fields.visitors.IdentityByConventionMappedFieldBuilderVisitor;
import com.github.restup.mapping.fields.visitors.JacksonMappedFieldBuilderVisitor;
import com.github.restup.registry.settings.AutoDetectConstants;
import com.github.restup.util.ReflectionUtils.BeanInfo;
import com.github.restup.util.ReflectionUtils.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 * Allows visiting a builder while building a {@link MappedField} using {@link DefaultMappedFieldFactory} for handling api, persistence, or other custom annotations.
 *
 * @author abuttaro
 */
@FunctionalInterface
public interface MappedFieldBuilderVisitor {

    static Builder builder() {
        return new Builder();
    }

    /**
     * Visit a {@link MappedField.Builder} during field mapping to provide customization of field attributes
     *
     * @param b builder to visit
     * @param bi describing the Class being mapped
     * @param pd describing the property being mapped
     * @param <T> type of class being mapped
     */
    <T> void visit(MappedField.Builder<T> b, BeanInfo<T> bi, PropertyDescriptor pd);

    class Builder {

        private boolean defaults;
        private List<MappedFieldBuilderVisitor> visitors = new ArrayList<>();

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
            return add(new IdentityByConventionMappedFieldBuilderVisitor(identityField));
        }

        public Builder add(MappedFieldBuilderVisitor visitor) {
            visitors.add(visitor);
            return me();
        }

        public MappedFieldBuilderVisitor[] build() {
            List<MappedFieldBuilderVisitor> result = new ArrayList<>(visitors);
            if (defaults) {
                result.add(new IdentityByConventionMappedFieldBuilderVisitor());

                if (AutoDetectConstants.JACKSON2_EXISTS) {
                    result.add(new JacksonMappedFieldBuilderVisitor());
                }
            }
            return result.toArray(new MappedFieldBuilderVisitor[0]);
        }

    }


}
