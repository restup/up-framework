package com.petstore;

import com.github.restup.annotations.ApiName;
import com.github.restup.annotations.Plural;
import com.github.restup.annotations.field.CaseInsensitive;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@ApiName("category")
@Plural("categories")
@Entity
public class Category {
    @Id
    @GeneratedValue
    private Long id;
    @CaseInsensitive(searchField = "nameLowerCase")
    private String name;
    private String nameLowerCase;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameLowerCase() {
        return nameLowerCase;
    }

    public void setNameLowerCase(String nameLowerCase) {
        this.nameLowerCase = nameLowerCase;
    }
}
