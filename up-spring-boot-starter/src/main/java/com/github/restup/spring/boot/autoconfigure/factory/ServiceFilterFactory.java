package com.github.restup.spring.boot.autoconfigure.factory;

public interface ServiceFilterFactory {

    default Object[] getServiceFilters() {
        return null;
    }

}
