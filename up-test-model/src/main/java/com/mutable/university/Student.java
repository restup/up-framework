package com.mutable.university;

import static com.university.Student.RESOURCE_NAME;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.restup.annotations.ApiName;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.SafeHtml;
import org.hibernate.validator.constraints.SafeHtml.WhiteListType;

@ApiName(value = RESOURCE_NAME)
@DynamoDBTable(tableName = "Student")
public class Student {

    @DynamoDBHashKey(attributeName = "Id")
    private String id;

    // demonstrate different api, bean, persisted paths
    @JsonProperty("firstName")
    @DynamoDBAttribute(attributeName = "FirstName")
    // use javax validations
    @SafeHtml(whitelistType = WhiteListType.NONE)
    @NotBlank
    private String fName;

    // demonstrate different api, bean, persisted paths
    @JsonProperty("lastName")
    @DynamoDBAttribute(attributeName = "LastName")
    // use javax validations
    @SafeHtml(whitelistType = WhiteListType.NONE)
    @NotBlank
    private String lName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

}
