package com.github.restup.controller;


import org.junit.Test;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.result.NegotiatedResult;
import com.github.restup.test.assertions.Assertions;

public class PojoTest {

    @Test
    public void testPojos() {
        String regex = "Basic((?!Test$).)*$";
        Assertions.pojo()
        .addMatching(1, ParsedResourceControllerRequest.class, "BasicParsed")
        .addMatching(3, NegotiatedResult.class, "((?!Negotiated).)*Result$")
        .validate();
    }
    
}
