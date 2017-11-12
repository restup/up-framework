package com.model.test.company;

import com.github.restup.annotations.field.Relationship;

public class Contractor extends Person {

    @Relationship(resource = Company.class)
    private Long companyId;

}
