package com.github.restup.test.model;

public class Foo {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getFoo() {
        throw new IllegalStateException();
    }
    
}
