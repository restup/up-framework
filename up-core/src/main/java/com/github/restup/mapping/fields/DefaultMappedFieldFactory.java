package com.github.restup.mapping.fields;

import static com.github.restup.util.ReflectionUtils.getAnnotation;
import static com.github.restup.util.ReflectionUtils.getGenericReturnType;
import static com.github.restup.util.ReflectionUtils.getReturnType;

import com.github.restup.annotations.field.CaseInsensitive;
import com.github.restup.annotations.field.Immutable;
import com.github.restup.annotations.field.Param;
import com.github.restup.annotations.field.Relationship;
import com.github.restup.mapping.fields.MappedField.Builder;
import com.github.restup.util.ReflectionUtils.BeanInfo;
import com.github.restup.util.ReflectionUtils.PropertyDescriptor;

/**
 * Default {@link MappedFieldFactory} implementation. <p> Provides default mapping and accepts {@link MappedFieldBuilderVisitor}s which will be applied to the {@link MappedField.Builder} allowing for overriding default mapping details. (For example, api, persistent names obtained from implementation specific annotations).
 */
public class DefaultMappedFieldFactory implements MappedFieldFactory {

    private final MappedFieldBuilderVisitor[] visitors;

    public DefaultMappedFieldFactory(MappedFieldBuilderVisitor... visitors) {
        this.visitors = visitors;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> MappedField<T> getMappedField(BeanInfo<T> bi, PropertyDescriptor pd) {
        Class<T> type = (Class) getReturnType(pd, bi.getType());

        Builder<T> b = MappedField.builder(type)
                .field(pd.getField())
                .getter(pd.getGetter())
                .setter(pd.getSetter())
                // by default, bean, api, and persisted names are all the same.
                .beanName(pd.getName())
                .apiName(pd.getName())
                .persistedName(pd.getName())
                .genericType(getGenericReturnType(pd))
                .caseInsensitive(getAnnotation(CaseInsensitive.class, pd))
                .relationship(getAnnotation(Relationship.class, pd))
                .immutable(getAnnotation(Immutable.class, pd))
                .param(getAnnotation(Param.class, pd));

        b.accept(visitors, bi, pd);

        return b.build();
    }

}
