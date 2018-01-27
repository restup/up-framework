package com.test.model.animals;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.restup.annotations.ApiName;

@ApiName("animal")
public class Zebra extends Animal {

    private final int numStripes;
    @JsonIgnore
    private final String nickName;

    public Zebra(Long id, String name, int numStripes, String nickName) {
        super(id, name);
        this.numStripes = numStripes;
        this.nickName = nickName;
    }

    public int getNumStripes() {
        return numStripes;
    }

    public String getNickName() {
        return nickName;
    }


}
