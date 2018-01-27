package com.github.restup.mapping.fields.composition;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import com.github.restup.mapping.fields.ReadWriteField;
import com.github.restup.util.Assert;

public class MapField<VALUE> implements ReadWriteField<Map<String, Object>, VALUE> {

    private final String key;
    
    //TODO type... deserialization will serialize 1 to int when should be Long... this may cause problems

    MapField(String key) {
        Assert.notEmpty(key, "key is required");
        this.key = key;
    }

    public static MapField<?> of(String key) {
        return new MapField<>(key);
    }

    @Override
    @SuppressWarnings({ "unchecked" })
	public VALUE readValue(Object o) {
        Map<String, Object> map = asMap(o);
        return (VALUE) map.get(key);
    }

    @Override
    public void writeValue(Map<String, Object> obj, VALUE value) {
        Map<String, Object> map = asMap(obj);
        map.put(key, value);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected Map<String, Object> asMap(Object o) {
        if (o instanceof Map) {
            return (Map) o;
        }
        throw new IllegalArgumentException("argument must be a Map");
    }

    @Override
    public Map<String, Object> createDeclaringInstance() {
        return new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public VALUE createInstance() {
        return (VALUE) new HashMap<>();
    }

    @Override
    public boolean isDeclaredBy(Class<?> clazz) {
        return Map.class.isAssignableFrom(clazz);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (! ( o instanceof MapField )) {
            return false;
        }
        MapField<?> that = (MapField<?>) o;
        return Objects.equals(key, that.key);
    }

}
