package com.model.test.company;

import com.github.restup.annotations.ApiName;
import com.github.restup.annotations.field.Relationship;
import com.github.restup.annotations.field.RelationshipType;
import java.util.List;

@ApiName("company")
public class Company {

    private String id;
    private String name;

    //TODO auto detect relationships based upon types
    @Relationship(resource = Person.class, type = RelationshipType.oneToMany)
    private List<Long> workers;

    public List<Long> getWorkers() {
        return workers;
    }

    public void setWorkers(List<Long> workers) {
        this.workers = workers;
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
}
