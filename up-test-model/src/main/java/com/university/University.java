package com.university;

import static com.university.University.PLURAL_NAME;
import static com.university.University.RESOURCE_NAME;
import static com.university.University.TABLE_NAME;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.restup.annotations.ApiName;
import com.github.restup.annotations.Plural;
import com.github.restup.annotations.field.CaseInsensitive;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.SafeHtml;
import org.hibernate.validator.constraints.SafeHtml.WhiteListType;

@Entity(name = TABLE_NAME)
@ApiName(value = RESOURCE_NAME)
@Plural(PLURAL_NAME)
@DynamoDBTable(tableName = "University")
public class University {

	public static final String RESOURCE_NAME = "university";
	public static final String PLURAL_NAME = "universities";
	public static final String TABLE_NAME = RESOURCE_NAME;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@DynamoDBHashKey(attributeName = "Id")
	private final Long id;

	// use javax validations
	@SafeHtml(whitelistType = WhiteListType.NONE)
	@NotBlank
	@CaseInsensitive(searchField = "nameUpperCase", lowerCased = false)
	@DynamoDBAttribute(attributeName = "Name")
	private final String name;

	@Column(name = "name_upper_case")
	@DynamoDBAttribute(attributeName = "NameUpperCase")
	@JsonIgnore
	private final String nameUpperCase;

	public University(Long id, String name, String nameUpperCase) {
		super();
		this.id = id;
		this.name = name;
		this.nameUpperCase = nameUpperCase;
	}

	public University() {
		// for Jackson deserialization
		this(null, null, null);
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getNameUpperCase() {
		return nameUpperCase;
	}

}
