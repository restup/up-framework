package com.test.model.animals;

import com.github.restup.annotations.ApiName;

@ApiName("animal")
@SuppressWarnings("unused")
public class Animal {

    private final Long id;
    private final String name;

	public Animal(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public Animal() {
		this(null, null);
	}

}
