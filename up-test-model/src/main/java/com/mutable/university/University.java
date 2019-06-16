package com.mutable.university;

import static com.university.University.PLURAL_NAME;
import static com.university.University.RESOURCE_NAME;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.restup.annotations.ApiName;
import com.github.restup.annotations.Plural;
import com.github.restup.annotations.field.CaseInsensitive;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.SafeHtml;
import org.hibernate.validator.constraints.SafeHtml.WhiteListType;

@ApiName(value = RESOURCE_NAME)
@Plural(PLURAL_NAME)
@DynamoDBTable(tableName = "University")
public class University {

    @DynamoDBHashKey(attributeName = "Id")
    private String id;

    // use javax validations
    @SafeHtml(whitelistType = WhiteListType.NONE)
    @NotBlank
    @CaseInsensitive(searchField = "nameUpperCase", lowerCased = false)
    @DynamoDBAttribute(attributeName = "Name")
    private String name;

    @DynamoDBAttribute(attributeName = "NameUpperCase")
    @JsonIgnore
    private String nameUpperCase;

    public University(String id, String name, String nameUpperCase) {
        super();
        this.id = id;
        this.name = name;
        this.nameUpperCase = nameUpperCase;
    }

    public University() {
        // for Jackson deserialization
        this(null, null, null);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameUpperCase() {
        return nameUpperCase;
    }

    public void setNameUpperCase(String nameUpperCase) {
        this.nameUpperCase = nameUpperCase;
    }

}
