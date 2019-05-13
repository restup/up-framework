package com.github.restup.mapping.fields;

class BasicMappedIndexField implements MappedIndexField {

    private final String indexName;
    private final Short position;

    BasicMappedIndexField(String indexName, Short position) {
        this.indexName = indexName;
        this.position = position;
    }

    @Override
    public String getIndexName() {
        return indexName;
    }

    @Override
    public Short getPosition() {
        return position;
    }
}
