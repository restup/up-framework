package com.github.restup.mapping.fields.composition;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class IdentifierTest {

    
    @Test
    public void testBuilder() {
        testBuilder(true);
        testBuilder(false);
    }

    private void testBuilder(boolean clientGeneratedIdentifierPermitted) {
        assertEquals(clientGeneratedIdentifierPermitted,
            Identifier.builder()
                .clientGeneratedIdentifierPermitted(clientGeneratedIdentifierPermitted)
                .build().isClientGeneratedIdentifierPermitted());
    }
    
}
