package com.model.test.company;

import com.github.restup.annotations.field.Immutable;

public class Employee extends Person {

    private String department;

    @Override
    @Immutable
    public Long getId() {
        return super.getId();
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

}
