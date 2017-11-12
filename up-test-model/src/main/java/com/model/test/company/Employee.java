package com.model.test.company;

import com.github.restup.annotations.field.Immutable;

@SuppressWarnings("unused")
public class Employee extends Person {

    @Immutable
    private Long id;
    private String department;

}
