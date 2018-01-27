package com.github.restup.test.resource;

class StringContents implements Contents {

    private final String value;

    StringContents(String value) {
        this.value = value;
    }

    @Override
    public byte[] getContentAsByteArray() {
        return value.getBytes();
    }

    @Override
    public String getContentAsString() {
        return value;
    }
}
