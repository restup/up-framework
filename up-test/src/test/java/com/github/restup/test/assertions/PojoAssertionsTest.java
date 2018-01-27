package com.github.restup.test.assertions;

import org.junit.Test;
import com.github.restup.test.ApiRequest;
import com.github.restup.test.resource.RelativeTestResource;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;

public class PojoAssertionsTest {


    @Test
    public void testPojos() {
        Assertions.pojo()
        .add(RelativeTestResource.class)
        .addMatching(2, ApiRequest.class, "Basic")
        .add(2, "com.github.restup.test.model")
        .validate();
    }

    @Test
    public void testPojosAddMatchingRecursively() {
        Assertions.pojo()
        .addMatchingRecursively(2, "com.github.restup.test.model", ".*")
        .with(new GetterMustExistRule(), new SetterMustExistRule())
        .with(new SetterTester(), new GetterTester())
        .includeDefaultRules(false)
        .includeDefaultTesters(false)
        .validate();
    }

    @Test
    public void testPojosAddRecursively() {
        Assertions.pojo()
        .addRecursively(2, "com.github.restup.test.model")
        .validate();
    }

    @Test
    public void testPojosAddMatching() {
        Assertions.pojo()
        .addMatching(2, "com.github.restup.test.model", ".*")
        .validate();
    }
    
}
