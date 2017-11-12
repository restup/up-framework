package com.github.restup.test.resource;

public class StringContents implements Contents {

    private final String value;

    public StringContents(String value) {
        this.value = value;
    }

    public byte[] getContentAsByteArray() {
        return value.getBytes();
    }

    public String getContentAsString() {
        return value;
    }
}
