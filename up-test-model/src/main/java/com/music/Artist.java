package com.music;

import com.github.restup.annotations.ApiName;

@ApiName("artist")
public class Artist {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
