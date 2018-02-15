package com.github.restup.controller;


import org.junit.Test;
import com.github.restup.jackson.mixins.JsonApiResultMixin;
import com.github.restup.jackson.mixins.JsonResultMixin;
import com.github.restup.jackson.mixins.LinksResultMixin;
import com.github.restup.test.assertions.Assertions;

public class PojoTest {

    @Test
    public void testPojos() {
        String regex = "Basic((?!Test$).)*$";
        Assertions.pojo()
                .addMatching(1, "com.github.restup.controller.model", "BasicParsed")
                .addMatching(3, "com.github.restup.controller.model.result", "((?!Negotiated).)*Result$")
                .addMatching(1, "com.github.restup.controller.settings", regex)
                .add(JsonApiResultMixin.class, JsonResultMixin.class, LinksResultMixin.class)
        .validate();
    }
    
}
