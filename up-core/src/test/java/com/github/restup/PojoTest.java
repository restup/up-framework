package com.github.restup;


import org.junit.Test;
import com.github.restup.mapping.BasicMappedClass;
import com.github.restup.mapping.UntypedClass;
import com.github.restup.test.assertions.Assertions;

public class PojoTest {

    @Test
    public void testPojos() {
        String regex = "Basic((?!Test$).)*$";
        Assertions.pojo()
        .add(BasicMappedClass.class, UntypedClass.class)
                .addMatching(4, "com.github.restup.errors", regex)
                .addMatching(5, "com.github.restup.mapping.fields.composition", regex)
                .addMatching(5, "com.github.restup.service.model.request", regex)
                .addMatching(4, "com.github.restup.service.model.response", regex)
                .addMatchingRecursively(6, "com.github.restup.registry", regex)
                .addMatching(7, "com.github.restup.path", ".+PathValue$")
        .validate();
    }
    
}
