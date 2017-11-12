package com.github.restup.bind;

import com.github.restup.bind.converter.ParameterConverter;
import com.github.restup.bind.converter.ParameterConverterFactory;
import com.github.restup.bind.param.ParameterProvider;
import com.github.restup.errors.ErrorBuilder;
import com.github.restup.errors.Errors;
import com.github.restup.mapping.MappedClass;
import com.github.restup.mapping.MappedClassFactory;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.registry.settings.RegistrySettings;
import com.github.restup.service.FilterChainContext;
import com.github.restup.util.Assert;
import com.github.restup.util.ReflectionUtils;

import java.util.Collection;

/**
 * Default {@link MethodArgumentFactory} used to instantiate filter method arguments
 * and bind (http) request parameters to the instantiated objects.
 */
public class DefaultMethodArgumentFactory extends SimpleMethodArgumentFactory {

    private final MappedClassFactory mappedClassFactory;
    private final ParameterConverterFactory parameterConverterFactory;

    public DefaultMethodArgumentFactory(RegistrySettings settings) {
        this.mappedClassFactory = settings.getMappedClassFactory();
        this.parameterConverterFactory = settings.getParameterConverterFactory();
        Assert.notNull(mappedClassFactory, "mappedClassFactory is required");
        Assert.notNull(parameterConverterFactory, "parameterConverterFactory is required");
    }

    @Override
    public <T> T newInstance(Class<T> clazz, FilterChainContext ctx, Errors errors) {
        T instance = super.newInstance(clazz);
        ParameterProvider parameterProvider = ctx.getParameterProvider();
        if (parameterProvider != null) {
            // get class mapping for the parameter
            MappedClass<T> mappedClass = mappedClassFactory.getMappedClass(clazz);
            if (mappedClass != null && mappedClass.getAttributes() != null) {
                // check all mapped fields for (those annotated as allowing)
                // parameterNames.
                for (MappedField<?> field : mappedClass.getAttributes()) {
                    if (field.getParameterNames() != null) {
                        // collect the values for the parameter names
                        Object value = collectValues(field, parameterProvider, errors);
                        // and apply the value to the instance if needed
                        if (value != null) {
                            field.writeValue(instance, value);
                        }
                    }
                }
            }
        }
        return instance;
    }

    private <T> Object collectValues(MappedField<T> field, ParameterProvider parameterProvider, Errors errors) {
        Object result = null;
        String firstParameterNameForErrorDetail = null;
        Collection<Object> collection = null;
        for (String parameterName : field.getParameterNames()) {
            // get parameter
            String[] values = parameterProvider.getParameter(parameterName);
            if (values != null) {
                for (String value : values) {
                    collection = getApplicableCollectionInstance(field, collection);
                    if (collection == null) {
                        // the type supports a single value
                        if (result != null) {
                            // if there is already a value, add an error
                            errors.addError(
                                    ErrorBuilder.builder().code("DUPLICATE_PARAMETER")
                                            .title("Duplicate parameter")
                                            .detail("Parameter was passed multiple times")
                                            .meta(firstParameterNameForErrorDetail, result)
                                            .meta(parameterName, value));
                        } else {
                            // otherwise store the error and set the value
                            firstParameterNameForErrorDetail = parameterName;
                            result = convert(parameterName, field, errors, value);
                        }
                    } else {
                        // convert and add the value to the collection
                        Object convertedValue = convert(parameterName, field, errors, value);
                        collection.add(convertedValue);
                        result = collection;
                    }
                }
            }

        }
        return result;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T> Object convert(String parameterName, MappedField<T> field, Errors errors, String value) {
        ParameterConverter converter = parameterConverterFactory.getConverter(String.class, field.getType());
        if (converter != null) {
            return converter.convert(parameterName, value, errors);
        }
        return value;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T> Collection<Object> getApplicableCollectionInstance(MappedField<T> field, Collection<Object> collection) {
        if (collection != null) {
            return collection;
        }
        if (Collection.class.isAssignableFrom(field.getType())) {
            return (Collection) ReflectionUtils.newInstance(field.getType());
        }
        return null;
    }


}
