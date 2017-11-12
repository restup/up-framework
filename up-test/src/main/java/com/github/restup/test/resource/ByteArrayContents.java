package com.github.restup.test.resource;

public class ByteArrayContents implements Contents {

    private final byte[] bytes;

    public ByteArrayContents(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getContentAsByteArray() {
        return bytes;
    }

    public String getContentAsString() {
        return new String(bytes);
    }
}
