package com.model.test.company;

import javax.validation.constraints.NotBlank;

public class Address {

    @NotBlank
    private String street;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }
}
