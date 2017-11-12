package com.university;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.restup.annotations.ApiName;
import com.github.restup.annotations.Plural;
import com.github.restup.annotations.field.CaseInsensitive;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.SafeHtml;
import org.hibernate.validator.constraints.SafeHtml.WhiteListType;

import javax.persistence.*;

import static com.university.University.*;

@Entity(name = TABLE_NAME)
@ApiName(value = RESOURCE_NAME)
@Plural(PLURAL_NAME)
public class University {
    public static final String RESOURCE_NAME = "university";
    public static final String PLURAL_NAME = "universities";
    public static final String TABLE_NAME = RESOURCE_NAME;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // use javax validations
    @SafeHtml(whitelistType = WhiteListType.NONE)
    @NotBlank
    @CaseInsensitive(searchField = "nameUpperCase", lowerCased = false)
    private String name;

    @Column(name = "name_upper_case")
    @JsonIgnore
    private String nameUpperCase;

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

    public String getNameUpperCase() {
        return nameUpperCase;
    }

    public void setNameUpperCase(String nameUpperCase) {
        this.nameUpperCase = nameUpperCase;
    }

}
