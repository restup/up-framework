package com.github.restup.mapping;

import com.github.restup.config.ConfigurationContext;
import com.github.restup.config.UpFactories;
import com.github.restup.util.Assert;
import com.github.restup.util.ReflectionUtils.BeanInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface MappedClassBuilderDecorator {

    static Builder builder() {
        return new Builder();
    }

    <T> void decorate(com.github.restup.mapping.MappedClass.Builder<T> builder, BeanInfo<T> bi);

    class Builder {

        private List<MappedClassBuilderDecorator> decorators = new ArrayList<>();
        private ConfigurationContext configurationContext;

        private Builder() {

        }

        private Builder me() {
            return this;
        }


        public Builder add(MappedClassBuilderDecorator decorator) {
            decorators.add(decorator);
            return me();
        }

        public Builder addAll(Collection<MappedClassBuilderDecorator> decorators) {
            if (decorators != null) {
                decorators.stream().forEach(this::add);
            }
            return me();
        }

        public Builder configurationContext(ConfigurationContext configurationContext) {
            this.configurationContext = configurationContext;
            return me();
        }

        public MappedClassBuilderDecorator[] build() {
            Assert.notNull(configurationContext, "configurationContext is required");
            List<MappedClassBuilderDecorator> result = new ArrayList<>(decorators);

            result.addAll(UpFactories.getInstance()
                .getInstances(configurationContext, MappedClassBuilderDecorator.class));
            return result.toArray(new MappedClassBuilderDecorator[0]);
        }

    }

}
