package com.mutable.university;

import static com.university.Course.RESOURCE_NAME;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.restup.annotations.ApiName;
import com.github.restup.annotations.Resource;
import com.github.restup.annotations.field.CaseInsensitive;
import com.github.restup.annotations.field.Relationship;
import javax.persistence.GeneratedValue;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.SafeHtml;
import org.hibernate.validator.constraints.SafeHtml.WhiteListType;

@ApiName(value = RESOURCE_NAME)
@DynamoDBTable(tableName = "Course")
@Resource(indexedQueryOnly = false)
public class Course {

    @DynamoDBHashKey(attributeName = "Id")
    @GeneratedValue
    private String id;

    // use javax validations
    @SafeHtml(whitelistType = WhiteListType.NONE)
    @NotBlank
    @CaseInsensitive(searchField = "nameLowerCase")
    @DynamoDBAttribute(attributeName = "Name")
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = "UniversityNameKey")
    private String name;

    @JsonIgnore
    @DynamoDBAttribute(attributeName = "NameLowerCase")
    private String nameLowerCase;

    // demonstrate different api/bean, persisted paths
    // Add a relationship between resources
    @Relationship(resource = University.class)
    // use javax validations
    @NotNull
    @DynamoDBAttribute(attributeName = "SchoolId")
    @DynamoDBIndexHashKey(globalSecondaryIndexName = "UniversityNameKey")
    private String universityId;

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

    public String getNameLowerCase() {
        return nameLowerCase;
    }

    public void setNameLowerCase(String nameLowerCase) {
        this.nameLowerCase = nameLowerCase;
    }

    public String getUniversityId() {
        return universityId;
    }

    public void setUniversityId(String universityId) {
        this.universityId = universityId;
    }

}
