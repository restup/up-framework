package com.university;

import static com.university.Course.RESOURCE_NAME;
import static com.university.Course.TABLE_NAME;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.restup.annotations.ApiName;
import com.github.restup.annotations.Resource;
import com.github.restup.annotations.field.CaseInsensitive;
import com.github.restup.annotations.field.Relationship;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.SafeHtml;
import org.hibernate.validator.constraints.SafeHtml.WhiteListType;

@Entity(name = TABLE_NAME)
@ApiName(value = RESOURCE_NAME)
@DynamoDBTable(tableName = "Course")
@Resource(indexedQueryOnly = false)
public class Course {

    public static final String RESOURCE_NAME = "course";
    public static final String PLURAL_NAME = "courses";
    public static final String TABLE_NAME = RESOURCE_NAME;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @DynamoDBHashKey(attributeName = "Id")
    private Long id;

    // use javax validations
    @SafeHtml(whitelistType = WhiteListType.NONE)
    @NotBlank
    @CaseInsensitive(searchField = "nameLowerCase")
    @DynamoDBAttribute(attributeName = "Name")
    private String name;

    @Column(name = "name_lower_case")
    @JsonIgnore
    @DynamoDBAttribute(attributeName = "NameLowerCase")
    private String nameLowerCase;

    // demonstrate different api/bean, persisted paths
    @Column(name = "school_id")
    // Add a relationship between resources
    @Relationship(resource = University.class)
    // use javax validations
    @NotNull
    @DynamoDBAttribute(attributeName = "SchoolId")
    private Long universityId;

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

    public Long getUniversityId() {
        return universityId;
    }

    public void setUniversityId(Long universityId) {
        this.universityId = universityId;
    }

}
