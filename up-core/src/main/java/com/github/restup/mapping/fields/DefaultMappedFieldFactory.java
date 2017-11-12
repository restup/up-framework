package com.github.restup.mapping.fields;

import com.github.restup.annotations.field.CaseInsensitive;
import com.github.restup.annotations.field.Immutable;
import com.github.restup.annotations.field.Param;
import com.github.restup.annotations.field.Relationship;
import com.github.restup.util.ReflectionUtils.*;

import static com.github.restup.util.ReflectionUtils.*;

/**
 * Default {@link MappedFieldFactory} implementation.
 * <p>
 * Provides default mapping and accepts {@link MappedFieldBuilderVisitor}s which
 * will be applied to the {@link MappedField.Builder} allowing
 * for overriding default mapping details. (For example, api, persistent names obtained from
 * implementation specific annotations).
 */
public class DefaultMappedFieldFactory implements MappedFieldFactory {

    private MappedFieldBuilderVisitor[] visitors;

    public DefaultMappedFieldFactory(MappedFieldBuilderVisitor... visitors) {
        this.visitors = visitors;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> MappedField<T> getMappedField(BeanInfo<T> bi, PropertyDescriptor pd) {
        Class<T> type = (Class) getReturnType(pd, bi.getType());

        MappedField.Builder<T> b = MappedField.builder(type)
                .setField(pd.getField())
                // by default, bean, api, and persisted names are all the same.
                .setBeanName(pd.getName())
                .setApiName(pd.getName())
                .setPersistedName(pd.getName())
                .setGenericType(getGenericReturnType(pd))
                .setCaseInsensitive(getAnnotation(CaseInsensitive.class, pd))
                .setRelationship(getAnnotation(Relationship.class, pd))
                .setImmutable(getAnnotation(Immutable.class, pd))
                .setParam(getAnnotation(Param.class, pd));

        b.accept(visitors, bi, pd);

        return b.build();
    }

}
