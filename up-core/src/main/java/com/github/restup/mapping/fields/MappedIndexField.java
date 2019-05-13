package com.github.restup.mapping.fields;

public interface MappedIndexField {

    static MappedIndexField of(String key, Short value) {
        return new BasicMappedIndexField(key, value);
    }

    String getIndexName();

    Short getPosition();
}
