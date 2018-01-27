package com.github.restup.test.resource;

class ByteArrayContents implements Contents {

    private final byte[] bytes;

    ByteArrayContents(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public byte[] getContentAsByteArray() {
        return bytes;
    }

    @Override
    public String getContentAsString() {
        return new String(bytes);
    }
}
