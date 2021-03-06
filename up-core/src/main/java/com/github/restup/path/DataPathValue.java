package com.github.restup.path;

import com.github.restup.mapping.fields.ReadableField;
import com.github.restup.service.model.ResourceData;

/**
 * {@link PathValue} of objects implementing {@link ResourceData}, such as "data" from requests and responses
 */
public class DataPathValue extends ConstantPathValue implements ReadableField<Object> {

    public static final String DATA = "data";

    private final static DataPathValue instance = new DataPathValue();

    static DataPathValue getInstance() {
        return instance;
    }

    private DataPathValue() {
        super(DATA);
    }

    @Override
    public boolean supportsType(Class<?> clazz) {
        return clazz != null && ResourceData.class.isAssignableFrom(clazz);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Object readValue(Object instance) {
        if (instance instanceof ResourceData) {
            return ((ResourceData) instance).getData();
        }
        return instance;
    }

}
