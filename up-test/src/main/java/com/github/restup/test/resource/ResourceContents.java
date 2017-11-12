package com.github.restup.test.resource;

public interface ResourceContents extends Contents {

    boolean exists();

    void writeResult(byte[] body);

}
