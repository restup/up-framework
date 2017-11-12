package com.model.test.company;

import com.github.restup.annotations.field.CaseInsensitive;
import com.github.restup.annotations.field.Immutable;

public class Person {

    @Immutable(errorOnUpdateAttempt = true)
    private Long id;
    @CaseInsensitive
    private String firstName;
    @CaseInsensitive(searchField = "lastNameLowerCased")
    private String lastName;
    private String lastNameLowerCased;
    private Address address;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastNameLowerCased() {
        return lastNameLowerCased;
    }

    public void setLastNameLowerCased(String lastNameLowerCased) {
        this.lastNameLowerCased = lastNameLowerCased;
    }
}
