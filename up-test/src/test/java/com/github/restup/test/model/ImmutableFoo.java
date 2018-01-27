package com.github.restup.test.model;

public class ImmutableFoo {

    private final String name;

    public ImmutableFoo(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
}
